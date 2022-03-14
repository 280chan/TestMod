package testmod.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import testmod.utils.MiscMethods;

public class ReflectPower extends AbstractTestPower implements MiscMethods {
	public static final String POWER_ID = "ReflectPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	public ReflectPower(AbstractCreature owner, int amount) {
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
    
    public void atEndOfTurn(final boolean isPlayer) {
    	if (isPlayer) {
    	    this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    	}
    }
    
    private static boolean checkType(CardType t) {
    	return !(t == CardType.CURSE || t == CardType.STATUS);
    }
    
	public void onUseCard(AbstractCard card, UseCardAction action) {
		if ((!card.isInAutoplay) && checkType(card.type) && (this.amount > 0)) {
			flash();
			AbstractMonster m = null;
			if (action.target != null) {
				m = (AbstractMonster) action.target;
			}
			this.playAgain(card, m);
			if (--this.amount == 0) {
				this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this));
			} else {
				this.updateDescription();
			}
		}
	}

}
