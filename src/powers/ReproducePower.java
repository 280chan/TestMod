package powers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class ReproducePower extends AbstractTestPower {
	public static final String POWER_ID = "ReproducePower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	private AbstractCard c;
	
	public ReproducePower(AbstractPlayer owner, AbstractCard card, int amount) {
		super(POWER_ID);
		this.c = card;
		this.name = NAME + "[" + c.name + "]";
		this.ID += c.cardID + c;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		this.description = DESCRIPTIONS[0] + c.name + DESCRIPTIONS[1];
	}
    
	public void stackPower(int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	public void onAfterCardPlayed(AbstractCard c) {
    	if (c.equals(this.c)) {
    		c.setCostForTurn(c.costForTurn + this.amount);
    		if (!c.returnToHand)
    			c.returnToHand = true;
    		if (!c.retain)
    			c.retain = true;
    	}
    }
	
	public void onRemove() {
		this.c.retain = false;
		this.c.returnToHand = false;
	}
}
