package powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class SuperconductorNoEnergyPower extends AbstractTestPower {
	public static final String POWER_ID = "SuperconductorNoEnergyPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	private int original;
	
	public SuperconductorNoEnergyPower(AbstractCreature owner, int original) {
		super(POWER_ID);
		this.name = NAME;
		this.owner = owner;
		this.amount = -1;
		this.original = original;
		this.checkZero();
		updateDescription();
		this.type = PowerType.DEBUFF;
	}
	
	public static void UpdateCurrentInstance() {
		for (AbstractPower p : AbstractDungeon.player.powers)
			if (p instanceof SuperconductorNoEnergyPower)
				((SuperconductorNoEnergyPower) p).checkEnergy();
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0];
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
    
    public void atEndOfTurn(final boolean isPlayer) {
    	this.addToBot(new RemoveSpecificPowerAction(owner, owner, this));
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
