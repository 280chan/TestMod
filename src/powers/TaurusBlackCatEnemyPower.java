package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import actions.DorothysBlackCatDamageAction;

public class TaurusBlackCatEnemyPower extends AbstractTestPower implements InvisiblePower {
	public static final String POWER_ID = "TaurusBlackCatEnemyPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final int PRIORITY = 100000;

	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof TaurusBlackCatEnemyPower);
	}
	
	public static AbstractPower getThis(AbstractCreature owner) {
		return owner.powers.stream().filter(p -> p instanceof TaurusBlackCatEnemyPower).findAny().orElse(null);
	}
	
	public TaurusBlackCatEnemyPower(AbstractCreature owner, int amount) {
		super(POWER_ID);
		this.name = NAME;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.DEBUFF;
		this.priority = PRIORITY;
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
    public int onAttacked(final DamageInfo info, final int damage) {
    	if (info.type != DamageType.HP_LOSS && damage / 100f * this.amount > 1)
    		this.addToBot(new DorothysBlackCatDamageAction(damage / 100f * this.amount));
    	return damage;
    }
    
}
