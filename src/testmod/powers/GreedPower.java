package testmod.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class GreedPower extends AbstractTestPower {
	
	public GreedPower(AbstractCreature owner, int amount) {
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.DEBUFF;
	}
	
	public void updateDescription() {
		 this.description = desc(0) + this.amount + desc(1) + this.amount + desc(2);
	}
	
	public void atEndOfTurn(final boolean isPlayer) {
		int handLeft = p().hand.size() * this.amount;
		int energyLeft = EnergyPanel.totalCount * this.amount;
		if (handLeft > 0)
			this.atb(this.apply(p(), new DischargePower(p(), this.amount)));
		if (energyLeft > 0)
			this.atb(this.apply(p(), new DrawDownPower(p(), this.amount)));
		this.atb(new RemoveSpecificPowerAction(this.owner, this.owner, this));
	}

}
