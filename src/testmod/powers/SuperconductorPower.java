package testmod.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class SuperconductorPower extends AbstractTestPower {
	
	public SuperconductorPower(AbstractCreature owner, int amount) {
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = desc(0) + this.amount + desc(1);
	}
    
	public void onCardDraw(AbstractCard c) {
		if (c.costForTurn > 0 && !c.freeToPlay() && this.amount > 0) {
			c.setCostForTurn(c.costForTurn - 1);
			this.amount--;
			if (this.amount == 0) {
				this.atb(new RemoveSpecificPowerAction(owner, owner, this));
			} else {
				this.updateDescription();
			}
		}
	}
    
}
