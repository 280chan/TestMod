package testmod.relicsup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
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
import testmod.actions.DaVinciLibraryAction;
import testmod.mymod.TestMod;
import testmod.relics.HeartOfDaVinci;
import testmod.utils.GetRelicTrigger;
import testmod.utils.MiscMethods;

public class HeartOfDaVinciUp extends AbstractUpgradedRelic implements MiscMethods, GetRelicTrigger, ClickableRelic {
	private static final ArrayList<AbstractRelic> ADDED = new ArrayList<AbstractRelic>();
	private static final ArrayList<AbstractRelic> COPY = new ArrayList<AbstractRelic>();
	private static HashMap<CardColor, HashMap<String, AbstractRelic>> map;
	private static DaVinciLibraryAction action;
	private static CardColor color = null;
	
	public HeartOfDaVinciUp() {
		super(RelicTier.UNCOMMON, LandingSound.MAGICAL);
	}
	
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
		ADDED.clear();
		Stream.of(RelicLibrary.redList, RelicLibrary.greenList, RelicLibrary.blueList, RelicLibrary.whiteList)
				.forEach(ADDED::addAll);
		(map = BaseMod.getAllCustomRelics()).values().stream().map(HashMap::values).forEach(ADDED::addAll);
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
	
	public void postUpdate() {
		if (this.isActive && action != null) {
			if (action.isDone) {
				action = null;
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
		if (this.isActive && this.relicStream(HeartOfDaVinci.class).count() == 0)
			this.addAllCharacterRelics();
		p().masterMaxOrbs += 1;
		this.counter = 0;
    }
	
	public void onUnequip() {
		if (this.isActive)
			this.removeAllCharacterRelics();
    }

	private void initAction(CardColor c) {
		action = new DaVinciLibraryAction(this.cards(c), AbstractDungeon.screen);
	}
	
	private Stream<HeartOfDaVinciUp> stream() {
		return this.relicStream(HeartOfDaVinciUp.class);
	}
	
	@Override
	public void receiveRelicGet(AbstractRelic r) {
		if (!this.isActive || COPY.remove(r))
			return;
		if (map == null)
			map = HeartOfDaVinci.map == null ? BaseMod.getAllCustomRelics() : HeartOfDaVinci.map;
		String name = this.name + ": ";
		TestMod.info(name + "获得遗物");
		if (r.tier != RelicTier.BOSS && this.canUpgrade(r)) {
			this.addTmpEffect(() -> {
				int i = p().relics.indexOf(r);
				if (i != -1) {
					TestMod.info(name + r.name + "可升级，进行升级");
					r.onUnequip();
					AbstractRelic u = this.tryUpgrade(r);
					COPY.add(u);
					p().relics.set(i, u);
					u.onEquip();
					p().reorganizeRelics();
				}
			});
		}
		CardColor c = getColor(r);
		if (c != null && c != getColor()) {
			color = c;
			stream().forEach(h -> h.counter++);
		}
	}

	@Override
	public void onRightClick() {
		if (!this.inCombat() && stream().anyMatch(r -> r.counter > 0) && color != null) {
			(this.counter > 0 ? this : stream().filter(r -> r.counter > 0).findFirst().get()).counter--;
			TestMod.info(name + "准备开始大图书馆");
			initAction(color);
			this.show();
		}
	}
	
}