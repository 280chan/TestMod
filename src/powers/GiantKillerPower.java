package powers;

import java.util.function.Consumer;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
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
		this.addMap(p -> new GiantKillerPower(p.owner));
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	private float finalDamage(float input, Consumer<GiantKiller> show) {
		return chain(relicStream(GiantKiller.class).peek(show).map(r -> get(this::damage))).apply(input);
	}
	
	private float damage(float input) {
		float tmp = input / p().maxHealth * this.owner.maxHealth;
		return tmp > Integer.MAX_VALUE || tmp < 0 ? Integer.MAX_VALUE : tmp;
	}
	
	public float atDamageFinalReceive(float damage, DamageType type) {
		return damage > 0 && type == DamageType.NORMAL && p().maxHealth > 0 && p().maxHealth < this.owner.maxHealth
				? finalDamage(damage, empty()) : damage;
	}
	
	public int onAttacked(DamageInfo info, int damage) {
		if (p().maxHealth < 1 && damage > 0)
			return Integer.MAX_VALUE;
		return damage > 0 && p().maxHealth < this.owner.maxHealth && info.type != DamageType.NORMAL
				? (int) finalDamage(damage, r -> r.show()) : damage;
	}

}
