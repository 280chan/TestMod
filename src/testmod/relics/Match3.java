package testmod.relics;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;

public class Match3 extends AbstractTestRelic {
	
	public Match3() {
		super(RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	private void act(int size) {
		this.show();
		this.addToBot(size % 3 == 0 ? new GainEnergyAction(1) : new DrawCardAction(1));
	}
	
	public void atTurnStartPostDraw() {
		act(p().relics.size());
		act(p().drawPile.size());
    }

}