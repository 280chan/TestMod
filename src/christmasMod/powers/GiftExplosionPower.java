package christmasMod.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.TheBombPower;

import powers.AbstractTestPower;

public class GiftExplosionPower extends AbstractTestPower {
	public static final String POWER_ID = "GiftExplosionPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	private static final int BOMB_AMOUNT = 3;
	private static final int BOMB_COUNT = 1;
	private int count;
	private int magicNumber;
	
	public GiftExplosionPower(AbstractCreature owner, int magicNumber) {
		super(POWER_ID);
		this.magicNumber = magicNumber;
		this.amount = magicNumber;
		this.name = NAME + this.magicNumber;
		this.ID += this.magicNumber;
		this.owner = owner;
		this.count = BOMB_COUNT;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.magicNumber + DESCRIPTIONS[1] + this.count + DESCRIPTIONS[2] + this.amount + DESCRIPTIONS[3];
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.count++;
	}
    
    public void atStartOfTurn() {
    	this.amount--;
    	if (this.amount <= 0) {
    		this.amount = this.magicNumber;
        	for (int i = 0; i < this.count; i++)
        		this.addToBot(new ApplyPowerAction(this.owner, this.owner, new TheBombPower(this.owner, BOMB_AMOUNT, 30), BOMB_AMOUNT));
    	}
    	this.updateDescription();
	}
    
}
