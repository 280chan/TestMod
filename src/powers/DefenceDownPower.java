package powers;

import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DefenceDownPower extends AbstractTestPower {
	public static final String POWER_ID = "DefenceDownPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	public static boolean hasThis(AbstractCreature owner) {
		for (AbstractPower p : owner.powers)
			if (p instanceof DefenceDownPower)
				return true;
		return false;
	}
	
	public DefenceDownPower(AbstractCreature owner, int amount) {
		super(POWER_ID);
		this.name = NAME;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.DEBUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
	}
	
    public float atDamageReceive(float damage, DamageType damageType) {
        return damage / 100f * (100 + this.amount);
    }
    
}
