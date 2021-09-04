package powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class GreedPower extends AbstractTestPower {
	public static final String POWER_ID = "GreedPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	public GreedPower(AbstractCreature owner, int amount) {
		super(POWER_ID);
		this.name = NAME;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.DEBUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
	}
	
    public void atEndOfTurn(final boolean isPlayer) {
    	AbstractPlayer p = AbstractDungeon.player;
    	int handLeft = p.hand.size() * this.amount;
    	int energyLeft = EnergyPanel.totalCount * this.amount;
		if (handLeft > 0)
			this.addToBot(new ApplyPowerAction(p, p, new DischargePower(p, handLeft), handLeft));
		if (energyLeft > 0)
			this.addToBot(new ApplyPowerAction(p, p, new DrawDownPower(p, energyLeft), energyLeft));
    	this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    }
    
}
