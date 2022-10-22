package testmod.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class InfectionSourcePower extends AbstractTestPower implements InvisiblePower {
	public static final String POWER_ID = "InfectionSourcePower";
	private static final int PRIORITY = 1000000;
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p ->  p instanceof InfectionSourcePower);
	}
	
	public InfectionSourcePower() {
		this.owner = p();
		this.amount = -1;
		updateDescription();
		this.type = PowerType.DEBUFF;
		this.priority = PRIORITY;
		this.addMap(p -> new InfectionSourcePower());
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	public float atDamageFinalGive(final float damage, final DamageInfo.DamageType type) {
		return type == DamageType.NORMAL ? damage * 0.5F : damage;
	}

}
