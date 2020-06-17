package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class InfectionSourcePower extends AbstractTestPower implements InvisiblePower {
	public static final String POWER_ID = "InfectionSourcePower";
	private static final int PRIORITY = 1000000;
	
	public static boolean hasThis(AbstractCreature owner) {
		for (AbstractPower p : owner.powers)
			if (p instanceof InfectionSourcePower)
				return true;
		return false;
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
        if (type == DamageType.NORMAL)
        	return damage * 0.5F;
    	return damage;
    }

    public void onRemove() {
		this.addToTop(new AbstractGameAction() {
			@Override
			public void update() {
				this.isDone = true;
				if (!hasThis(InfectionSourcePower.this.owner))
					InfectionSourcePower.this.owner.powers.add(new InfectionPower(InfectionSourcePower.this.owner));
			}
		});
	}

}
