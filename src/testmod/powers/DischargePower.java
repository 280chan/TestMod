package testmod.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class DischargePower extends AbstractTestPower {
	
	public DischargePower(AbstractCreature owner, int amount) {
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.DEBUFF;
	}
	
	public void updateDescription() {
		 this.description = desc(0) + this.amount + desc(1);
	}
    
    public void atEnergyGain() {
    	this.addToBot(new LoseEnergyAction(this.amount));
    	this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    }

}
