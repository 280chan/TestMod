package testmod.screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import basemod.BaseMod;
import testmod.mymod.TestMod;
import testmod.relics.AbstractTestRelic;
import testmod.relics.Gather;
import testmod.relicsup.GatherUp;
import testmod.utils.MiscMethods;

public class GatherSelectScreen extends RelicSelectScreen implements MiscMethods {
	private static final UIStrings UI = MISC.uiString();
	private static HashMap<AbstractRelic, AbstractRelic> map = new HashMap<AbstractRelic, AbstractRelic>();
	private boolean upgrade = false;
	
	public GatherSelectScreen(ArrayList<AbstractRelic> list, boolean autoSort, boolean upgrade) {
		super(copyList(list), true, autoSort);
		this.setDescription("", "", UI.TEXT[0]);
		this.renderAmount = true;
		this.upgrade = upgrade;
	}
	
	private static ArrayList<AbstractRelic> copyList(ArrayList<AbstractRelic> list) {
		return list.stream().map(MISC.split(r -> r.makeCopy(), MISC.t()))
				.peek(p -> map.put(p.getKey(), p.getValue())).map(p -> p.getKey()).collect(MISC.toArrayList());
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
		rewards(this.selectedRelic).stream().map(r -> tryUp(r).makeCopy()).forEach(r -> r.instantObtain());
		p().reorganizeRelics();
		int n = (int) (this.relicStream(Gather.class).count() + this.relicStream(GatherUp.class).count());
		if (n >= AbstractDungeon.getMonsters().monsters.stream().filter(m -> !(m.isDead || m.isDying)).count()) {
			this.addTmpActionToTop(GatherSelectScreen::winCombat);
			AbstractDungeon.getMonsters().monsters.stream().map(InstantKillAction::new).forEach(this::att);
		} else {
			ArrayList<AbstractMonster> l = AbstractDungeon.getMonsters().monsters.stream()
					.filter(m -> !(m.isDead || m.isDying)).collect(toArrayList());
			Collections.shuffle(l);
			l.stream().limit(n).map(InstantKillAction::new).forEach(this::att);
		}
		map.clear();
		AbstractDungeon.topPanel.adjustRelicHbs();
		GatherUp.trigger();
	}
	
	private AbstractRelic tryUp(AbstractRelic r) {
		return (this.upgrade && r instanceof AbstractTestRelic && ((AbstractTestRelic) r).canUpgrade())
				? ((AbstractTestRelic) r).upgrade() : r;
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
		GatherUp.trigger();
	}
	
	private RelicTier t(AbstractRelic r) {
		return relicPool(r.tier) == null ? RelicTier.RARE : r.tier;
	}

	private boolean valid(String id, AbstractRelic r) {
		return Stream.of(r.relicId, "Tiny House", "Orrery", "Pandora's Box", "Calling Bell", "Astrolabe",
				"Bottled Flame", "Bottled Lightning", "Bottled Tornado").noneMatch(id::equals);
	}
	
	private ArrayList<AbstractRelic> rewards(AbstractRelic r) {
		int n = (int) (this.relicStream(Gather.class).count() + this.relicStream(GatherUp.class).count());
		ArrayList<String> l = relicPool(t(r)).stream().filter(id -> valid(id, r)).collect(toArrayList());
		if (n > l.size()) {
			ArrayList<String> all = BaseMod.listAllRelicIDs().stream()
					.filter(id -> RelicLibrary.getRelic(id).tier == t(r) && valid(id, r)).collect(toArrayList());
			Collections.shuffle(all, new Random(AbstractDungeon.relicRng.randomLong()));
			all.stream().limit(n - l.size()).forEach(l::add);
			all.clear();
			return l.stream().map(RelicLibrary::getRelic).peek(TestMod::removeFromPool).collect(toArrayList());
		} else {
			Collections.shuffle(l, new Random(AbstractDungeon.relicRng.randomLong()));
			return l.stream().limit(n).map(RelicLibrary::getRelic).peek(TestMod::removeFromPool).collect(toArrayList());
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