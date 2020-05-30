package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class ResetBuffAction extends AbstractGameAction {

	private float startingDuration;
	private AbstractCreature t;
	private AbstractPower p;
	private int index;
	
	public ResetBuffAction(AbstractCreature t, AbstractPower p, int amount, int preIndex) {
		this.amount = 1;
		this.actionType = AbstractGameAction.ActionType.REDUCE_POWER;
		this.startingDuration = Settings.ACTION_DUR_FAST;
		this.duration = this.startingDuration;
		this.t = t;
		this.p = p;
		this.amount = amount;
		this.index = preIndex;
	}
	
	@Override
	public void update() {
		if (index == -2) {
			AbstractPower p = t.getPower(this.p.ID);
			p.amount = amount;
			p.updateDescription();
		} else {
			p.amount = amount;
			p.updateDescription();
			if (index < t.powers.size() && index > -1)
				t.powers.add(index, p);
			else
				t.powers.add(p);
		}
		this.isDone = true;
	}

}
