package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class TriggerFirstTurnPostDrawPowersAction extends AbstractGameAction {

	public TriggerFirstTurnPostDrawPowersAction() {
		this.actionType = ActionType.SPECIAL;
	}
	
	public void update() {
		for (AbstractPower p : AbstractDungeon.player.powers) {
			p.atStartOfTurnPostDraw();
		}
		this.isDone = true;
	}
	
}