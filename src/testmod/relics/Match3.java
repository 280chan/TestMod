package testmod.relics;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;

public class Match3 extends AbstractTestRelic {
	
	private void act(int size) {
		this.show();
		this.atb(size % 3 == 0 ? new GainEnergyAction(1) : new DrawCardAction(1));
	}
	
	public void atTurnStartPostDraw() {
		act(p().relics.size());
		act(p().drawPile.size());
	}

}