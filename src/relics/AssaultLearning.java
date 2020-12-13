package relics;

import actions.AssaultLearningAction;

public class AssaultLearning extends AbstractTestRelic {
	public static final String ID = "AssaultLearning";

	public AssaultLearning() {
		super(ID, RelicTier.COMMON, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atTurnStartPostDraw() {
		this.addToBot(new AssaultLearningAction(this));
    }
	
}