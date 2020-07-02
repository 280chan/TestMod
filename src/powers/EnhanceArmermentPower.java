package powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class EnhanceArmermentPower extends AbstractTestPower {
	public static final String POWER_ID = "EnhanceArmermentPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	private static final int PRIORITY = 99000;
	
	public EnhanceArmermentPower(AbstractCreature owner, int amount) {
		super(POWER_ID);
		this.name = NAME;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
		this.priority = PRIORITY;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.multiplier() + DESCRIPTIONS[1];
	}

	private int multiplier() {
		int m = 2;
    	for (int i = 1; i < this.amount; i++)
    		m *= 2;
    	return m;
	}
	
	public float atDamageFinalGive(float damage, DamageType type) {
        if (type == DamageType.NORMAL)
        	return damage * this.multiplier();
		return damage;
    }
    
    public void onUseCard(AbstractCard card, UseCardAction action) {
    	if (card.type == CardType.ATTACK) {
    		action.exhaustCard = true;
    		this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    	}
    }
    
}
