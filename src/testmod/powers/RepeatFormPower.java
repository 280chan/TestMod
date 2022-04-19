package testmod.powers;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class RepeatFormPower extends AbstractTestPower {
	private AbstractCard c;
	
	public RepeatFormPower(AbstractPlayer owner, int amount, AbstractCard card) {
		this.c = card.makeStatEquivalentCopy();
		this.c.resetAttributes();
		this.name += "[" + c.name + "]";
		this.ID += c.cardID + " " + c.timesUpgraded + " " + c.cost + " " + c.magicNumber;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		this.description = desc(0) + this.amount + desc(1) + this.c.name + desc(2);
	}
	
    public void atStartOfTurn() {
    	this.addToBot(new MakeTempCardInHandAction(this.c.makeStatEquivalentCopy(), this.amount));
    }
    
}
