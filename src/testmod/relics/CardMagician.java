package testmod.relics;

import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;

public class CardMagician extends AbstractTestRelic {

	public void onEquip() {
		this.addEnergy();
    }
	
	public void onUnequip() {
		this.reduceEnergy();
    }
	
	public void atTurnStart() {
	    if (!p().discardPile.isEmpty())
	    	this.atb(new EmptyDeckShuffleAction());
	    this.atb(new ShuffleAction(p().drawPile));
    }
	
}