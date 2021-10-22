package relics;

import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;

public class CardMagician extends AbstractTestRelic {
	
	public CardMagician() {
		super(RelicTier.BOSS, LandingSound.MAGICAL);
	}

	public void onEquip() {
		this.addEnergy();
    }
	
	public void onUnequip() {
		this.reduceEnergy();
    }
	
	public void atTurnStart() {
	    if (!p().discardPile.isEmpty())
	    	this.addToBot(new EmptyDeckShuffleAction());
	    this.addToBot(new ShuffleAction(p().drawPile));
    }
	
}