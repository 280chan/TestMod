package testmod.relics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import testmod.actions.DaVinciLibraryAction;
import testmod.mymod.TestMod;
import testmod.relicsup.HeartOfDaVinciUp;
import testmod.utils.GetRelicTrigger;
import testmod.utils.MiscMethods;

public class HeartOfDaVinci extends AbstractTestRelic implements MiscMethods, GetRelicTrigger {
	public static HashMap<CardColor, HashMap<String, AbstractRelic>> map;
	private static final ArrayList<AbstractRelic> ADDED = new ArrayList<AbstractRelic>();
	private static DaVinciLibraryAction action;
	private static int actionInQueue = 0;
	private static CardColor color = null;
	
	public static void clear() {
		action = null;
	}
	
	private void addIfPossible(ArrayList<String> pool, String id) {
		if (pool == null || pool.contains(id) || p().hasRelic(id))
			return;
		pool.add((int) (Math.random() * pool.size()), id);
	}
	
	public ArrayList<String> relicPool(RelicTier t) {
		switch (t) {
		case STARTER:
		case SPECIAL:
		case DEPRECATED:
			return AbstractDungeon.rareRelicPool;
		default:
			return MiscMethods.super.relicPool(t);
		}
	}
	
	private void addToRelicPool(AbstractRelic r) {
		this.addIfPossible(this.relicPool(r.tier), r.relicId);
	}
	
	private void addAllCharacterRelics() {
		Stream.of(RelicLibrary.redList, RelicLibrary.greenList, RelicLibrary.blueList, RelicLibrary.whiteList)
				.forEach(ADDED::addAll);
		
		(map = BaseMod.getAllCustomRelics()).values().stream().map(HashMap::values).forEach(ADDED::addAll);
		
		if (Loader.isModLoaded("RelicFilterMod")) {
			try {
				SpireConfig cfg = ReflectionHacks.getPrivateStatic(Class.forName("mymod.RelicFilterMod"), "config");
				ADDED.removeIf(r -> cfg.getBool(r.relicId));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		ADDED.forEach(this::addToRelicPool);
	}
	
	private void removeAllCharacterRelics() {
		PlayerClass c = p().chosenClass;

		ArrayList<String> remove = AbstractDungeon.floorNum < 1 ? new ArrayList<String>()
				: p().relics.stream().map(r -> r.relicId).collect(toArrayList());
		
		Stream.of(RelicTier.COMMON, RelicTier.UNCOMMON, RelicTier.RARE, RelicTier.SHOP, RelicTier.BOSS)
				.map(this.split(this.t(), this::relicPool)).forEach(consumer((t, pool) -> {
					pool.clear();
					RelicLibrary.populateRelicPool(pool, t, c);
					Collections.shuffle(pool, new Random(AbstractDungeon.relicRng.randomLong()));
					pool.removeAll(remove);
				}));
		
		remove.clear();
	}
	
	private void tryAddOrbSlot() {
		if (p().masterMaxOrbs == 0) {
			p().masterMaxOrbs = 1;
		}
	}
	
	public void postUpdate() {
		if (this.isActive && action != null && this.relicStream(HeartOfDaVinciUp.class).count() == 0) {
			if (action.isDone) {
				if (actionInQueue > 0) {
					initAction(color);
					actionInQueue--;
					TestMod.info("开始新一轮选牌");
				} else {
					action = null;
					color = null;
					TestMod.info("结束选牌");
				}
			} else {
				action.update();
			}
		}
	}
	
	private ArrayList<AbstractCard> cards(CardColor color) {
		ArrayList<AbstractCard> pool = CardLibrary.cards.entrySet().stream()
				.filter(c -> Settings.treatEverythingAsUnlocked() || !UnlockTracker.isCardLocked(c.getKey()))
				.map(Map.Entry::getValue).filter(c -> c.color == color && c.rarity != CardRarity.BASIC)
				.collect(toArrayList());
		Collections.shuffle(pool, new Random(AbstractDungeon.cardRng.randomLong()));
		if (pool.size() < 20) {
			TestMod.info("WTF! This character has less than 20 cards");
		}
		return pool.stream().limit(20).map(AbstractCard::makeCopy).collect(this.toArrayList());
	}
	
	private CardColor getColor() {
		return p().getCardColor();
	}
	
	private CardColor getColor(AbstractRelic r) {
		return isGreen(r) ? CardColor.GREEN
				: (isRed(r) ? CardColor.RED
						: (isBlue(r) ? CardColor.BLUE : (isPurple(r) ? CardColor.PURPLE : getCustomColor(r))));
	}
	
	private CardColor getCustomColor(AbstractRelic r) {
		return map.keySet().stream().filter(c -> inList(r, map.get(c).values())).findFirst().orElse(null);
	}
	
	private boolean isGreen(AbstractRelic r) {
		return this.inList(r, RelicLibrary.greenList);
	}

	private boolean isRed(AbstractRelic r) {
		return this.inList(r, RelicLibrary.redList);
	}

	private boolean isBlue(AbstractRelic r) {
		return this.inList(r, RelicLibrary.blueList);
	}
	
	private boolean isPurple(AbstractRelic r) {
		return this.inList(r, RelicLibrary.whiteList);
	}

	private boolean inList(AbstractRelic relic, Collection<AbstractRelic> list) {
		return list.stream().map(r -> r.relicId).anyMatch(relic.relicId::equals);
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.isActive && this.relicStream(HeartOfDaVinciUp.class).count() == 0) {
			this.addAllCharacterRelics();
			this.tryAddOrbSlot();
		}
    }
	
	public void onUnequip() {
		if (this.relicStream(HeartOfDaVinci.class).count() == 1
				&& this.relicStream(HeartOfDaVinciUp.class).count() == 0)
			this.removeAllCharacterRelics();
	}
	
	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.actNum < 2;
	}

	private void initAction(CardColor c) {
		action = new DaVinciLibraryAction(this.cards(c), AbstractDungeon.screen);
	}
	
	@Override
	public void receiveRelicGet(AbstractRelic r) {
		if (!this.isActive && this.relicStream(HeartOfDaVinciUp.class).count() != 0)
			return;
		if (map == null)
			map = BaseMod.getAllCustomRelics();
		String name = this.name + ": ";
		TestMod.info(name + "获得遗物");
		CardColor c = getColor(r);
		if (c == null) {
			TestMod.info(name + "非角色限定遗物");
			return;
		}
		if (c != getColor()) {
			TestMod.info(name + "准备开始大图书馆");
			initAction(color = c);
			actionInQueue = (int) this.relicStream(HeartOfDaVinci.class).peek(a -> a.flash()).count() - 1;
			TestMod.info("队列：" + actionInQueue);
		} else {
			TestMod.info(name + "角色本身遗物");
		}
	}
	
}