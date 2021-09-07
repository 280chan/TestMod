package powers;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Reboot;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class ShutDownPower extends AbstractTestPower {
	public static final String POWER_ID = "ShutDownPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	private boolean upgraded;
	
	public ShutDownPower(AbstractCreature owner, boolean upgraded, int amount) {
		super(POWER_ID);
		this.name = NAME;
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
		this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + (this.upgraded ? "+" : DESCRIPTIONS[2]);
	}
	
    public void atStartOfTurn() {
    	AbstractCard c = new Reboot();
    	if (upgraded)
    		c.upgrade();
		this.addToBot(new MakeTempCardInHandAction(c, this.amount));
    }
    
}
