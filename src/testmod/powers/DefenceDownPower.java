package testmod.powers;

import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import testmod.relics.AscensionHeart;

public class DefenceDownPower extends AbstractTestPower {
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof DefenceDownPower);
	}
	
	public DefenceDownPower(AbstractCreature owner, int amount) {
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.DEBUFF;
	}
	
	public void updateDescription() {
		 this.description = desc(0) + (single() ? amount : (dmgRate(100f) - 100)) + desc(1);
	}
	
	private float dmg(float input) {
		return input * (100 + this.amount) / 100;
	}
	
	private float dmgRate(float input) {
		return chain(relicStream(AscensionHeart.class).map(r -> get(this::dmg))).apply(input);
	}
	
    public float atDamageReceive(float damage, DamageType damageType) {
        return dmgRate(damage);
    }
    
    private boolean single() {
		return relicStream(AscensionHeart.class).count() == 1;
	}
}
