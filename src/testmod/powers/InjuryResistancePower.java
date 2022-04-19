package testmod.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.core.AbstractCreature;

import testmod.relics.InjuryResistance;

public class InjuryResistancePower extends AbstractTestPower implements InvisiblePower {
	private static final int PRIORITY = 1000000;
	private InjuryResistance r;
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof InjuryResistancePower);
	}
	
	public InjuryResistancePower(AbstractCreature owner, InjuryResistance r) {
		this.owner = owner;
		this.amount = r.counter;
		this.r = r;
		updateDescription();
		this.type = PowerType.BUFF;
		this.priority = PRIORITY;
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
		this.amount += stackAmount;
		this.r.counter = this.amount;
	}
	
    public int onLoseHp(int damage) {
    	if (this.amount > 0) {
    		damage -= this.amount;
    		if (damage <= 0) {
    			damage = 0;
    			this.amount = 0;
    		} else {
    			this.amount++;
    		}
    		this.r.show();
    	} else {
    		this.amount++;
    	}
		this.r.counter = this.amount;
		this.updateDescription();
        return damage;
    }
    
}
