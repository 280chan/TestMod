package testmod.relics;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import testmod.mymod.TestMod;

public class Metronome extends AbstractTestRelic {
	public static final String ID = "Metronome";
	public ArrayList<String> cards = new ArrayList<String>();
	
	public Metronome() {
		super(RelicTier.UNCOMMON, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return this.cards == null || this.cards.size() == 0 ? DESCRIPTIONS[0]
				: DESCRIPTIONS[0] + cards.stream().map(this::getName).reduce(DESCRIPTIONS[1], (a, b) -> a + ", " + b);
	}

	private String getName(String id) {
		AbstractCard c = CardLibrary.getCard(id);
		if (c == null) print("Can't find card \"" + id + "\" in CardLibrary, F");
		return c == null ? id : c.name;
	}
	
	private void act() {
		if (this.counter > 0) {
			this.relicStream(Metronome.class).peek(r -> r.show()).forEach(r -> p().increaseMaxHp(this.counter, true));
		}
		this.stopPulse();
		this.counter = -1;
	}
	
	public static void load() {
		MISC.relicStream(Metronome.class).filter(r -> r.isActive).forEach(r -> r.loadList());
	}
	
	private static void save(ArrayList<String> list) {
		TestMod.saveString(ID, list);
	}
	
	private void loadList() {
		this.setList(TestMod.getStringList(ID));
	}
	
	private ArrayList<String> getList() {
		return AbstractDungeon.actionManager.cardsPlayedThisCombat.stream().map(c -> c.cardID).collect(toArrayList());
	}
	
	private ArrayList<String> setList(ArrayList<String> list) {
		this.cards.clear();
		this.cards.addAll(list);
		this.updateDescription();
		return this.cards;
	}
	
	public void onUseCard(final AbstractCard c, final UseCardAction a) {
		if (this.counter == -1 || this.counter > this.cards.size())
			return;
		if (this.counter == this.cards.size() || !this.cards.get(this.counter).equals(c.cardID)) {
			this.act();
		} else {
			this.counter++;
		}
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.isActive) {
			save(cards);
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
			save(setList(getList()));
		}
    }
	
	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.actNum < 3;
	}

}