package powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class AutoReboundPower extends AbstractTestPower {
	public static final String POWER_ID = "AutoReboundPower";
	private static final String REGION = "rebound";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	private int cardsReboundedThisTurn = 0;
	
	public AutoReboundPower(AbstractCreature owner, int amount) {
		super(POWER_ID, REGION);
		this.name = NAME;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
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
