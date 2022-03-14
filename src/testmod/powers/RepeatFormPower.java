package testmod.powers;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class RepeatFormPower extends AbstractTestPower {
	public static final String POWER_ID = "RepeatFormPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	private AbstractCard c;
	
	public RepeatFormPower(AbstractPlayer owner, int amount, AbstractCard card) {
		super(POWER_ID);
		this.c = card.makeStatEquivalentCopy();
		this.c.resetAttributes();
		this.name = NAME + "[" + c.name + "]";
		this.ID += c.cardID + c.timesUpgraded + c.cost + c.magicNumber;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.c.name + DESCRIPTIONS[2];
	}
	
    public void atStartOfTurn() {
    	this.addToBot(new MakeTempCardInHandAction(this.c.makeStatEquivalentCopy(), this.amount));
    }
    
}
