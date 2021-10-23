package powers;

import java.util.function.Function;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import relics.GiantKiller;
import utils.MiscMethods;

public class GiantKillerPower extends AbstractTestPower implements InvisiblePower, MiscMethods {
	public static final String POWER_ID = "GiantKillerPower";
	
	public GiantKillerPower(AbstractCreature owner) {
		super(POWER_ID);
		this.name = POWER_ID;
		this.owner = owner;
		this.amount = -1;
		updateDescription();
		this.type = PowerType.DEBUFF;
		this.priority = -10000;
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	private float finalDamage(float input) {
		return this.getIdenticalList((Function<Float, Float>) this::damage, GiantKiller.countGiantKiller()).stream()
				.reduce(t(), Function::andThen).apply(input);
	}
	
	private float damage(float input) {
		float tmp = input / p().maxHealth * this.owner.maxHealth;
		return tmp > Integer.MAX_VALUE || tmp < 0 ? Integer.MAX_VALUE : tmp;
	}
	
	public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
		if (damage > 0) {
			if (p().maxHealth < 1)
				return Integer.MAX_VALUE;
			return p().maxHealth < this.owner.maxHealth ? finalDamage(damage) : damage;
		}
		return damage;
	}
	
	public void onRemove() {
		this.addTmpActionToTop(() -> GiantKiller.addIfNotHave(this.owner));
	}

}
