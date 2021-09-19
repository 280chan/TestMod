package relics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import actions.DaVinciLibraryAction;
import basemod.BaseMod;
import mymod.TestMod;
import utils.MiscMethods;

public class HeartOfDaVinci extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "HeartOfDaVinci";
	private static final ArrayList<AbstractRelic> ADDED = new ArrayList<AbstractRelic>();
	private static DaVinciLibraryAction action;
	private static int numRelics = 0;
	public static boolean updated = false;
	
	public static void updateThis() {
		if (AbstractDungeon.player.relics.stream().anyMatch(r -> r instanceof HeartOfDaVinci) && checkGetRelic())
			getThis().onGetRelic(AbstractDungeon.player.relics.get(size()));
	}
	
	public static HeartOfDaVinci getThis() {
		return (HeartOfDaVinci) AbstractDungeon.player.relics.stream().filter(r -> r instanceof HeartOfDaVinci)
				.findFirst().orElse(null);
	}
	
	public HeartOfDaVinci() {
		super(ID, RelicTier.UNCOMMON, LandingSound.MAGICAL);
	}
	
	public static void clear() {
		action = null;
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	private void addIfPossible(ArrayList<String> pool, String id) {
		if (pool.contains(id) || AbstractDungeon.player.hasRelic(id))
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
	
	private static HashMap<CardColor, HashMap<String, AbstractRelic>> map;
	
	private void addAllCharacterRelics() {
		ADDED.addAll(RelicLibrary.redList);
		ADDED.addAll(RelicLibrary.greenList);
		ADDED.addAll(RelicLibrary.blueList);
		
		(map = BaseMod.getAllCustomRelics()).values().stream().map(HashMap<String, AbstractRelic>::values)
				.forEach(ADDED::addAll);
		ADDED.forEach(this::addToRelicPool);
	}
	
	private void removeAllCharacterRelics(AbstractPlayer p) {
		PlayerClass c = p.chosenClass;
		ArrayList<ArrayList<String>> pools = new ArrayList<ArrayList<String>>();

		ArrayList<String> remove = AbstractDungeon.relicsToRemoveOnStart;
		remove.clear();
		Iterator<AbstractRelic> var1;
		if (AbstractDungeon.floorNum >= 1) {
			var1 = p.relics.iterator();
			while (var1.hasNext()) {
				AbstractRelic r = (AbstractRelic) var1.next();
				remove.add(r.relicId);
			}
		}
		
		pools.add(AbstractDungeon.commonRelicPool);
		pools.add(AbstractDungeon.uncommonRelicPool);
		pools.add(AbstractDungeon.rareRelicPool);
		pools.add(AbstractDungeon.shopRelicPool);
		pools.add(AbstractDungeon.bossRelicPool);
		
		final RelicTier[] TIERS = {RelicTier.COMMON, RelicTier.UNCOMMON, RelicTier.RARE, RelicTier.SHOP, RelicTier.BOSS};
		
		for (int i = 0; i < pools.size(); i++) {
			ArrayList<String> pool = pools.get(i);
			pool.clear();
			RelicLibrary.populateRelicPool(pool, TIERS[i], c);
			Collections.shuffle(pool, new Random(AbstractDungeon.relicRng.randomLong()));
			
			Iterator<String> var2 = remove.iterator();
			while (var2 != null && var2.hasNext()) {
				if (this.remove(pool.iterator(), var2.next())) {
					var2.remove();
				}
			}
		}
		
	}
	
	private boolean remove(Iterator<String> s, String string) {
		while (s.hasNext()) {
			if (s.next().equals(string)) {
				s.remove();
				return true;
			}
		}
		return false;
	}
	
	private void tryAddOrbSlot() {
		if (AbstractDungeon.player.masterMaxOrbs == 0) {
			AbstractDungeon.player.masterMaxOrbs = 1;
		}
	}
	
	public void postUpdate() {
		if (action != null) {
			if (action.isDone) {
				action = null;
				TestMod.info("结束当前选牌");
			} else {
				action.update();
			}
		}
	}
	
	public void onGetRelic(AbstractRelic r) {
		if (!updated)
			return;
		numRelics++;
		String name = this.name + ": ";
		TestMod.info(name + "获得遗物");
		CardColor c = getColor(r);
		if (c == null) {
			TestMod.info(name + "非角色限定遗物");
			return;
		}
		if (c != getColor(AbstractDungeon.player)) {
			TestMod.info(name + "准备开始大图书馆");
			action = new DaVinciLibraryAction(this.cards(c), AbstractDungeon.screen);
		} else {
			TestMod.info(name + "角色本身遗物");
		}
	}
	
	private ArrayList<AbstractCard> cards(CardColor color) {
		ArrayList<AbstractCard> pool = CardLibrary.cards.entrySet().stream()
				.filter(c -> Settings.treatEverythingAsUnlocked() || !UnlockTracker.isCardLocked(c.getKey()))
				.map(Map.Entry::getValue).filter(c -> c.color == color && c.rarity != CardRarity.BASIC)
				.map(AbstractCard::makeCopy).collect(Collectors.toCollection(ArrayList::new));
		if (pool.size() < 20) {
			TestMod.info("WTF! This character has less than 20 cards");
		}
		return pool;
	}
	
	private CardColor getColor(AbstractPlayer p) {
		return p.getCardColor();
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
	
	public static boolean checkGetRelic() {
		return numRelics < AbstractDungeon.player.relics.size();
	}
	
	public static int size() {
		return numRelics;
	}
	
	public static void init(int size) {
		if (numRelics != size || !updated) {
			numRelics = size;
			map = BaseMod.getAllCustomRelics();
			updated = true;
		}
	}
	
	public void onEquip() {
		numRelics = AbstractDungeon.player.relics.size();
		this.addAllCharacterRelics();
		this.tryAddOrbSlot();
    }
	
	public void onUnequip() {
		this.removeAllCharacterRelics(AbstractDungeon.player);
    }
	
	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.actNum < 2;
	}
	
}