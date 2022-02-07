package screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import basemod.BaseMod;
import mymod.TestMod;
import relics.Gather;
import utils.MiscMethods;

public class GatherSelectScreen extends RelicSelectScreen implements MiscMethods {
	private static final UIStrings UI = INSTANCE.uiString();
	
	private static HashMap<AbstractRelic, AbstractRelic> map = new HashMap<AbstractRelic, AbstractRelic>();
	
	public GatherSelectScreen(ArrayList<AbstractRelic> list, boolean autoSort, String bDesc) {
		super(copyList(list), true, autoSort);
		this.setDescription(bDesc, "", UI.TEXT[0]);
		this.renderAmount = true;
	}
	
	private static ArrayList<AbstractRelic> copyList(ArrayList<AbstractRelic> list) {
		return list.stream().map(INSTANCE.split(r -> r.makeCopy(), INSTANCE.t()))
				.peek(p -> map.put(p.getKey(), p.getValue())).map(p -> p.getKey()).collect(INSTANCE.toArrayList());
	}
	
	@Override
	protected void afterSelected() {
		AbstractRelic tmp = map.get(this.selectedRelic);
		if (p().relics.contains(tmp)) {
			remove(tmp);
			print("移除成功");
		} else {
			remove(p().getRelic(tmp.relicId));
			print("移除失败");
		}
		rewards(this.selectedRelic).stream().peek(TestMod::removeFromPool).forEach(r -> r.instantObtain());
		p().reorganizeRelics();
		winCombat();
		map.clear();
		AbstractDungeon.topPanel.adjustRelicHbs();
	}

	private void remove(AbstractRelic r) {
		if (r == null)
			return;
		r.onVictory();
		r.onUnequip();
		p().relics.remove(r);
	}
	
	@Override
	protected void afterCanceled() {
		p().reorganizeRelics();
		map.clear();
	}
	
	private RelicTier t(AbstractRelic r) {
		return relicPool(r.tier) == null ? RelicTier.RARE : r.tier;
	}

	private boolean valid(String id, AbstractRelic r) {
		return Stream.of(r.relicId, "Tiny House", "Orrery", "Pandora's Box", "Calling Bell", "Astrolabe",
				"Bottled Flame", "Bottled Lightning", "Bottled Tornado").noneMatch(id::equals);
	}
	
	private ArrayList<AbstractRelic> rewards(AbstractRelic r) {
		int n = (int) this.relicStream(Gather.class).count();
		ArrayList<String> tmp = relicPool(t(r)).stream().filter(id -> valid(id, r)).collect(toArrayList());
		if (n > tmp.size()) {
			ArrayList<String> all = BaseMod.listAllRelicIDs().stream()
					.filter(id -> RelicLibrary.getRelic(id).tier == t(r) && valid(id, r)).collect(toArrayList());
			Collections.shuffle(all, new Random(AbstractDungeon.relicRng.randomLong()));
			all.stream().limit(n - tmp.size()).forEach(tmp::add);
			all.clear();
			return tmp.stream().map(RelicLibrary::getRelic).map(a -> a.makeCopy()).collect(toArrayList());
		} else {
			Collections.shuffle(tmp, new Random(AbstractDungeon.relicRng.randomLong()));
			return tmp.stream().limit(n).map(RelicLibrary::getRelic).map(a -> a.makeCopy()).collect(toArrayList());
		}
	}
	
	@Override
	protected String categoryOf(AbstractRelic r) {
		return Gather.f(map.get(r)) + "";
	}

	@Override
	protected String descriptionOfCategory(String category) {
		return UI.TEXT[1] + category + UI.TEXT[2];
	}

	private static void winCombat() {
		if (AbstractDungeon.currMapNode != null) {
			AbstractDungeon.getCurrRoom().endBattle();
		} else {
			System.out.println("Fxxk you! Author to the mod that starts combat without a room!");
			AbstractDungeon.player.onVictory();
		}
	}

	@Override
	protected void addRelics() {
	}
}