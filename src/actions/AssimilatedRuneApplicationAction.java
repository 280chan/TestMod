package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;

import powers.AssimilatedRunePower;

public class AssimilatedRuneApplicationAction extends AbstractGameAction {

	public static final float DURATION = Settings.ACTION_DUR_FAST;

	private AssimilatedRunePower p;
	
	public AssimilatedRuneApplicationAction(AssimilatedRunePower p) {
		this.amount = p.amount;
		this.actionType = ActionType.POWER;
		this.duration = DURATION;
		this.p = p;
		this.target = p.owner;
	}

	@Override
	public void update() {
		if (this.target.hasPower(p.ID)) {
			this.target.getPower(p.ID).stackPower(this.amount);
			this.target.getPower(p.ID).updateDescription();
		} else {
			this.target.powers.add(0, p);
		}
		this.isDone = true;
	}

}
