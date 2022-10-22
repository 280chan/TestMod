package testmod.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class SuperconductorNoEnergyPower extends AbstractTestPower {
	private int original;
	
	public SuperconductorNoEnergyPower(AbstractCreature owner, int original) {
		this.owner = owner;
		this.amount = -1;
		this.original = original;
		this.checkZero();
		updateDescription();
		this.type = PowerType.DEBUFF;
	}
	
	public static void UpdateCurrentInstance() {
		AbstractDungeon.player.powers.stream().filter(p -> p instanceof SuperconductorNoEnergyPower)
				.map(p -> (SuperconductorNoEnergyPower) p).forEach(SuperconductorNoEnergyPower::checkEnergy);
	}
	
	public void updateDescription() {
		 this.description = desc(0);
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	public void atEndOfTurn(final boolean isPlayer) {
		this.atb(new RemoveSpecificPowerAction(owner, owner, this));
	}

	public void checkEnergy() {
		if (EnergyPanel.totalCount > this.original) {
			EnergyPanel.totalCount = this.original;
		} else if (EnergyPanel.totalCount < this.original) {
			this.original = EnergyPanel.totalCount;
			this.checkZero();
		}
	}
	
	private void checkZero() {
		if (this.original < 0)
			this.original = 0;
	}

}
