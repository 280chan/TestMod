package testmod.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;

import testmod.relics.OneHitWonder;
import testmod.relicsup.OneHitWonderUp;

public class OneHitWonderDebuffPower extends AbstractTestPower implements InvisiblePower {
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof OneHitWonderDebuffPower);
	}
	
	public OneHitWonderDebuffPower(AbstractCreature owner) {
		this.owner = owner;
		this.amount = -1;
		updateDescription();
		this.type = PowerType.DEBUFF;
		this.addMap(p -> new OneHitWonderDebuffPower(p.owner));
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	private boolean checkPlayerHealth() {
		return countUp() == 0 ? p().currentHealth == 1 : p().currentHealth < 10;
	}
	
	private long count() {
		return this.relicStream(OneHitWonder.class).count();
	}
	
	private long countUp() {
		return this.relicStream(OneHitWonderUp.class).count();
	}
	
	private float f(float multiplier, float multiplier1, float input) {
		return (float) (Math.pow(multiplier, count()) * Math.pow(multiplier1, countUp()) * input);
	}
	
	public int onAttacked(DamageInfo info, int damage) {
		return checkPlayerHealth() && info.type != DamageType.NORMAL ? (int) f(1.5f, 3f, damage) : damage;
	}
	
	public float atDamageFinalGive(float damage, DamageType type) {
		return checkPlayerHealth() ? f(0.5f, 0.25f, damage) : damage;
	}
    
	public float atDamageFinalReceive(float damage, DamageType type) {
		return checkPlayerHealth() && type == DamageType.NORMAL ? f(1.5f, 3f, damage) : damage;
	}
    
}
