package testmod.relicsup;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import testmod.mymod.TestMod;
import testmod.relics.Metronome;
import testmod.utils.CounterKeeper;

public class MetronomeUp extends AbstractUpgradedRelic implements CounterKeeper {
	public static final String ID = "MetronomeUp";
	public ArrayList<CardType> cards = new ArrayList<CardType>();
	
	public MetronomeUp() {
		super(RelicTier.UNCOMMON, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return this.cards == null || this.cards.size() == 0 ? DESCRIPTIONS[0]
				: DESCRIPTIONS[0] + cards.stream().map(this::getName).reduce(DESCRIPTIONS[1], (a, b) -> a + ", " + b);
	}
	
	public void run(AbstractRelic r, AbstractUpgradedRelic u) {
		((Metronome) r).cards.stream().map(id -> CardLibrary.getCard(id)).filter(c -> c != null).map(c -> c.type)
				.forEach(((MetronomeUp) u).cards::add);
		save(((MetronomeUp) u).cards);
		u.updateDescription();
	}

	private String getName(CardType t) {
		return t.ordinal() < 5 ? DESCRIPTIONS[t.ordinal() + 2] : t.name();
	}
	
	private void act() {
		if (this.counter > 0) {
			this.relicStream(MetronomeUp.class).peek(r -> r.show()).forEach(r -> p().increaseMaxHp(this.counter, true));
		}
		this.stopPulse();
		this.counter = -1;
	}
	
	public static void load() {
		MISC.relicStream(MetronomeUp.class).filter(r -> r.isActive).forEach(r -> r.loadList());
	}
	
	private static void save(ArrayList<CardType> list) {
		TestMod.saveString(ID, list.stream().map(s -> s.name()).collect(MISC.toArrayList()));
	}
	
	private void loadList() {
		this.setList(TestMod.getStringList(ID));
	}
	
	private ArrayList<CardType> getList() {
		return AbstractDungeon.actionManager.cardsPlayedThisCombat.stream().map(c -> c.type).collect(toArrayList());
	}
	
	private ArrayList<CardType> setList(ArrayList<String> list) {
		this.cards.clear();
		list.stream().map(s -> CardType.valueOf(CardType.class, s)).forEach(this.cards::add);
		this.updateDescription();
		return this.cards;
	}
	
	private ArrayList<CardType> set(ArrayList<CardType> list) {
		this.cards.clear();
		this.cards.addAll(list);
		this.updateDescription();
		list.clear();
		return this.cards;
	}
	
	public void onUseCard(final AbstractCard c, final UseCardAction a) {
		if (this.counter == -1 || this.counter > this.cards.size())
			return;
		if (this.counter == this.cards.size() || this.cards.get(this.counter) != c.type) {
			this.act();
		} else {
			this.counter++;
		}
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.isActive) {
			if (inCombat()) {
				this.counter = -1;
			}
		}
    }
	
	public void onUnequip() {
		this.cards.clear();
		if (this.isActive)
			save(cards);
    }
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		this.counter = 0;
		if (this.cards.size() == 0)
			loadList();
		if (this.cards.size() > 0)
			this.beginLongPulse();
		else
			this.counter = -1;
    }
	
	public void onVictory() {
		this.act();
		if (this.isActive) {
			save(set(getList()));
		}
    }

}