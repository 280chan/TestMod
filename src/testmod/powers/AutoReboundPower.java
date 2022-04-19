package testmod.powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class AutoReboundPower extends AbstractTestPower {
	private static final String REGION = "rebound";
	private int cardsReboundedThisTurn = 0;
	
	public AutoReboundPower(AbstractCreature owner, int amount) {
		this.setRegion(REGION);
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = desc(0) + this.amount + desc(1);
	}
	
	public void atStartOfTurn() {
		this.cardsReboundedThisTurn = 0;
	}
	
	private boolean checkAmount() {
		return this.cardsReboundedThisTurn < this.amount;
	}
    
	public void onAfterUseCard(final AbstractCard c, final UseCardAction action) {
		if (c.type != CardType.POWER && !c.exhaust && !c.exhaustOnUseOnce && !action.exhaustCard && !c.purgeOnUse) {
			if (c.type != CardType.CURSE && c.type != CardType.STATUS && checkAmount()) {
				flash();
				action.reboundCard = true;
				this.cardsReboundedThisTurn++;
			}
		}
	}
    
}
