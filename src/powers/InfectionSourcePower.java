package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;

import utils.MiscMethods;

public class InfectionSourcePower extends AbstractTestPower implements InvisiblePower, MiscMethods {
	public static final String POWER_ID = "InfectionSourcePower";
	private static final int PRIORITY = 1000000;
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> {return p instanceof InfectionSourcePower;});
	}
	
	public InfectionSourcePower(AbstractCreature owner) {
		super(POWER_ID);
		this.name = POWER_ID;
		this.owner = owner;
		this.amount = -1;
		updateDescription();
		this.type = PowerType.DEBUFF;
		this.priority = PRIORITY;
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

    public void onRemove() {
		this.addTmpActionToTop(() -> {
			if (!hasThis(this.owner))
				this.owner.powers.add(new InfectionSourcePower(this.owner));
		});
	}

}
