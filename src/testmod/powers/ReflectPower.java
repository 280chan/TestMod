package testmod.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ReflectPower extends AbstractTestPower {
	
	public ReflectPower(AbstractCreature owner, int amount) {
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = desc(0) + this.amount + desc(1);
	}
	
	public void atEndOfTurn(final boolean isPlayer) {
		if (isPlayer) {
			this.atb(new RemoveSpecificPowerAction(this.owner, this.owner, this));
		}
	}
	
	private static boolean checkType(CardType t) {
		return t != CardType.CURSE && t != CardType.STATUS;
	}
	
	public void onUseCard(AbstractCard card, UseCardAction action) {
		if ((!card.isInAutoplay) && checkType(card.type) && (this.amount > 0)) {
			flash();
			AbstractMonster m = null;
			if (action.target != null && action.target instanceof AbstractMonster) {
				m = (AbstractMonster) action.target;
			}
			this.playAgain(card, m);
			if (--this.amount == 0) {
				this.att(new RemoveSpecificPowerAction(this.owner, this.owner, this));
			} else {
				this.updateDescription();
			}
		}
	}

}
