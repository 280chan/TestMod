package relics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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

public class HeartOfDaVinci extends AbstractTestRelic{
	
	public static final String ID = "HeartOfDaVinci";
	
	private static final ArrayList<AbstractRelic> ADDED = new ArrayList<AbstractRelic>();
	private static DaVinciLibraryAction action;
	private static int numRelics = 0;
	public static boolean updated = false;
	
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
	
	private void addToRelicPool(AbstractRelic r) {
		switch (r.tier) {
		case BOSS:
			this.addIfPossible(AbstractDungeon.bossRelicPool, r.relicId);
			break;
		case COMMON:
			this.addIfPossible(AbstractDungeon.commonRelicPool, r.relicId);
			break;
		case RARE:
			this.addIfPossible(AbstractDungeon.rareRelicPool, r.relicId);
			break;
		case SHOP:
			this.addIfPossible(AbstractDungeon.shopRelicPool, r.relicId);
			break;
		case STARTER:
			this.addIfPossible(AbstractDungeon.rareRelicPool, r.relicId);
			break;
		case UNCOMMON:
			this.addIfPossible(AbstractDungeon.uncommonRelicPool, r.relicId);
			break;
		default:
			TestMod.info("达芬奇之心: 非合理类型遗物:" + r.tier + "," + r.name + ", 姑且加入进稀有池");
			this.addIfPossible(AbstractDungeon.rareRelicPool, r.relicId);
			break;
		}
	}
	
	private static HashMap<CardColor, HashMap<String, AbstractRelic>> map;
	
	private void addAllCharacterRelics() {
		ADDED.addAll(RelicLibrary.redList);
		ADDED.addAll(RelicLibrary.greenList);
		ADDED.addAll(RelicLibrary.blueList);
		
		map = BaseMod.getAllCustomRelics();
		ArrayList<HashMap<String, AbstractRelic>> all = new ArrayList<HashMap<String, AbstractRelic>>();
		if (all.addAll(map.values())) {
			for (HashMap<String, AbstractRelic> character : all) {
				ADDED.addAll(character.values());
			}
		}
		
		for (AbstractRelic r : ADDED) {
			this.addToRelicPool(r);
		}
		
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
		ArrayList<AbstractCard> pool = new ArrayList<AbstractCard>();
		CardColor c = getColor(r);
		if (c == null) {
			TestMod.info(name + "非角色限定遗物");
			return;
		}
		if (c != getColor(AbstractDungeon.player)) {
			this.addCards(pool, c);
			TestMod.info(name + "准备开始大图书馆");
			action = new DaVinciLibraryAction(pool, AbstractDungeon.screen);
		} else {
			TestMod.info(name + "角色本身遗物");
		}
	}
	
	private void addCards(ArrayList<AbstractCard> pool, CardColor color) {
		AbstractCard card = null;
		for (Map.Entry<String, AbstractCard> c : CardLibrary.cards.entrySet()) {
			card = (AbstractCard) c.getValue();
			if ((card.color == color) && (card.rarity != CardRarity.BASIC)
					&& ((!UnlockTracker.isCardLocked((String) c.getKey())) || (Settings.treatEverythingAsUnlocked()))) {
				pool.add(card);
			}
		}
		if (pool.size() < 20) {
			TestMod.info("WTF! This character has less than 20 cards");
		}
	}
	
	private CardColor getColor(AbstractPlayer p) {
		return p.getCardColor();
	}
	
	private CardColor getColor(AbstractRelic r) {
		if (isGreen(r))
			return CardColor.GREEN;
		if (isRed(r))
			return CardColor.RED;
		if (isBlue(r))
			return CardColor.BLUE;
		return getCustomColor(r);
	}
	
	private CardColor getCustomColor(AbstractRelic r) {
		Set<CardColor> colors = map.keySet();
		for (CardColor c : colors) {
			ArrayList<AbstractRelic> relics = new ArrayList<AbstractRelic>();
			relics.addAll(map.get(c).values());
			if (inList(r, relics)) {
				return c;
			}
		}
		return null;
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

	private boolean inList(AbstractRelic relic, ArrayList<AbstractRelic> l) {
		for (AbstractRelic r : l) {
			if (r.relicId.equals(relic.relicId)) {
				return true;
			}
		}
		return false;
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
		if (!Settings.isEndless && AbstractDungeon.actNum > 1) {
			return false;
		}
		return true;
	}
	
}