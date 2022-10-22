package testmod.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;

public class Restrained extends AbstractTestRelic {

	public void onEquip() {
		this.addEnergy();
	}
	
	public void onUnequip() {
		this.reduceEnergy();
	}

	public void atTurnStart() {
		this.counter++;
	}
	
	public void atPreBattle() {
		this.counter = 0;
	}

	public void onVictory() {
		this.counter = -1;
	}
	
	public boolean canPlay(AbstractCard c) {
		return this.counter > 1 || c.type != CardType.POWER;
	}
}