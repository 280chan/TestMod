package powers;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.PoisonPower;

import relics.InfectionSource;

public class InfectionPower extends AbstractTestPower implements InvisiblePower {
	public static final String POWER_ID = "InfectionPower";
	private float tempDamage;
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof InfectionPower);
	}
	
	public InfectionPower(AbstractCreature owner) {
		super(POWER_ID);
		this.name = POWER_ID;
		this.owner = owner;
		this.amount = -1;
		updateDescription();
		this.type = PowerType.DEBUFF;
		this.addMap(p -> new InfectionPower(p.owner));
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}

	public float atDamageFinalReceive(float damage, DamageType type) {
		return this.tempDamage = damage;
	}
    
    public int onAttacked(final DamageInfo info, final int damageAmount) {
    	if (info.type == DamageType.NORMAL) {
    		int dmg = MathUtils.floor(tempDamage);
        	if (dmg > 0) {
				this.relicStream(InfectionSource.class).peek(r -> r.show())
						.forEach(r -> this.addToTop(this.apply(p(), new PoisonPower(owner, p(), dmg))));
				if (damageAmount > 0) {
        			return 1;
        		}
        	}
    	}
    	return damageAmount;
    }

}
