package powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class AutoReboundPower extends AbstractPower {
	public static final String POWER_ID = "AutoReboundPower";
	public static final String NAME = "自动弹回";
	public static final String[] DESCRIPTIONS = {"你每回合打出的前 #b", " 张可被弹回的牌，将回到抽牌堆顶部。"};
	private int cardsReboundedThisTurn = 0;
	
	public AutoReboundPower(AbstractCreature owner, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		loadRegion("rebound");
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
    
    public void onAfterUseCard(final AbstractCard card, final UseCardAction action) {
		if ((card.type != AbstractCard.CardType.POWER) && (!card.exhaust) && (!card.exhaustOnUseOnce) && !action.exhaustCard && !card.purgeOnUse) {
			if (card.type != CardType.CURSE && card.type != CardType.STATUS && checkAmount()) {
				flash();
				action.reboundCard = true;
				this.cardsReboundedThisTurn++;
			}
		}
    }
    
}
