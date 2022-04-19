package testmod.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class DrawDownPower extends AbstractTestPower {
	private static final String REGION = "lessdraw";
	
	public DrawDownPower(AbstractCreature owner, int amount) {
		this.setRegion(REGION);
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.DEBUFF;
	}
	
	public void updateDescription() {
		 this.description = desc(0) + this.amount + desc(1);
	}
	
	public void onInitialApplication() {
		p().gameHandSize -= this.amount;
	}

	public void stackPower(int amt) {
		super.stackPower(amt);
		p().gameHandSize -= amt;
		updateDescription();
	}

	public void atStartOfTurnPostDraw() {
		this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
	}

	public void onRemove() {
		p().gameHandSize += this.amount;
	}

}
