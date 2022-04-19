package testmod.powers;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Reboot;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class ShutDownPower extends AbstractTestPower {
	private boolean upgraded;
	
	public ShutDownPower(AbstractCreature owner, boolean upgraded, int amount) {
		if (upgraded)
			this.name += "+";
		this.ID += upgraded;
		this.owner = owner;
		this.amount = amount;
		this.upgraded = upgraded;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		this.description = desc(0) + this.amount + desc(1) + (this.upgraded ? "+" : desc(2));
	}
	
    public void atStartOfTurn() {
    	AbstractCard c = new Reboot();
    	if (upgraded)
    		c.upgrade();
		this.addToBot(new MakeTempCardInHandAction(c, this.amount));
    }
    
}
