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
		if (this.target.powers.stream().anyMatch(p -> p instanceof AssimilatedRunePower)) {
			this.p = (AssimilatedRunePower) this.target.powers.stream().filter(p -> p instanceof AssimilatedRunePower)
					.findFirst().get();
			this.p.stackPower(this.amount);
			this.p.updateDescription();
		} else {
			this.target.powers.add(0, p);
		}
		this.isDone = true;
	}

}
