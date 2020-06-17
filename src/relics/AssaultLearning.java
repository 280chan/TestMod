package relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import actions.AssaultLearningAction;

public class AssaultLearning extends AbstractTestRelic {
	public static final String ID = "AssaultLearning";

	public AssaultLearning() {
		super(ID, RelicTier.UNCOMMON, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atTurnStartPostDraw() {
		if (this.isActive)
			AbstractDungeon.actionManager.addToBottom(new AssaultLearningAction(this));
    }
	
}