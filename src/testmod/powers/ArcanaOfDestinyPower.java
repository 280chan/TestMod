package testmod.powers;

import java.util.function.UnaryOperator;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import testmod.relics.ArcanaOfDestiny;

public class ArcanaOfDestinyPower extends AbstractTestPower implements InvisiblePower {
	public static final String POWER_ID = "ArcanaOfDestinyPower";
	
	public static boolean hasThis(AbstractCreature m) {
		return m.powers.stream().anyMatch(p -> p instanceof ArcanaOfDestinyPower);
	}
	
	public static void addThis(AbstractCreature m) {
		m.powers.add(new ArcanaOfDestinyPower(m));
	}
	
	public ArcanaOfDestinyPower(AbstractCreature owner) {
		this.owner = owner;
		this.amount = -1;
		updateDescription();
		this.type = PowerType.DEBUFF;
		this.addMap(p -> new ArcanaOfDestinyPower(p.owner));
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	public float HPRate(AbstractCreature p, float f) {
		return p == null || p.maxHealth == 0 ? f : p.currentHealth * 1f / p.maxHealth;
	}
	
	private float damage(float damage) {
		float tmp = HPRate(this.owner, 1f), p = HPRate(p(), 1f);
		return tmp > p ? damage * (1 - tmp + p) : damage;
	}
	
	private float attack(float attack) {
		float tmp = HPRate(this.owner, 0f), p = HPRate(p(), 1f);
		return tmp < p ? (attack * (1 + 2 * (p - tmp))) : attack;
	}
	
	private <T> UnaryOperator<T> repeat(UnaryOperator<T> f) {
		return chain(relicStream(ArcanaOfDestiny.class).map(r -> f));
	}
	
	public float atDamageGive(float damage, DamageType type) {
		return repeat(this::damage).apply(damage);
	}
	
	public int onAttacked(DamageInfo info, int damage) {
		if (damage > 0) {
			this.addTmpActionToTop(() -> ((AbstractMonster) this.owner).applyPowers());
			return repeat(this::attack).apply(damage * 1f).intValue();
		}
		return 0;
	}
	
	public int onHeal(int amount) {
		if (amount > 0) {
			this.addTmpActionToTop(() -> ((AbstractMonster) this.owner).applyPowers());
		}
		return amount;
	}

}
