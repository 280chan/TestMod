
package powers;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import relics.D_4.Situation;

public class D_4Power extends AbstractTestPower {
	public static final String POWER_ID = "D_4Power";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	private static final int AMOUNT = -1;
	public Situation situation;
	public static boolean endTurn = false;
	private static final int PRIORITY = -100000;
	
	public static boolean hasThis(AbstractPlayer p) {
		for (AbstractPower po : p.powers)
			if (po instanceof D_4Power)
				return true;
		return false;
	}
	
	public static String getString(Situation s) {
		if (s != null && s.ordinal() < 4)
			return DESCRIPTIONS[s.ordinal() + 1];
		return DESCRIPTIONS[5];
	}
	
	public D_4Power(AbstractCreature owner, Situation s) {
		super(POWER_ID + s.ordinal());
		this.name = NAME;
		this.owner = owner;
		this.amount = AMOUNT;

		this.situation = s;
		updateDescription();
		this.type = PowerType.BUFF;
		this.priority = PRIORITY;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + situation.toString() + DESCRIPTIONS[6];
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount = -1;
	}
    
}
