package relics;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;

public class Match3 extends AbstractTestRelic {
	
	public Match3() {
		super(RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	private void act(int size) {
		if (size % 3 != 0)
			return;
		this.addToBot(new GainEnergyAction(1));
		this.show();
	}
	
	public void atTurnStartPostDraw() {
		act(p().relics.size());
		act(p().drawPile.size());
    }

}