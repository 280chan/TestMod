package testmod.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class SuperconductorPower extends AbstractTestPower {
	public static final String POWER_ID = "SuperconductorPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	public SuperconductorPower(AbstractCreature owner, int amount) {
		super(POWER_ID);
		this.name = NAME;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
	}
    
	public void onCardDraw(AbstractCard c) {
		if (c.costForTurn > 0 && !c.freeToPlayOnce && this.amount > 0) {
			c.setCostForTurn(c.costForTurn - 1);
			this.amount--;
			if (this.amount == 0) {
				this.addToBot(new RemoveSpecificPowerAction(owner, owner, this));
			} else {
				this.updateDescription();
			}
		}
	}
    
}
