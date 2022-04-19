package testmod.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DeathImprintPower extends AbstractTestPower {
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof DeathImprintPower);
	}
	
	public static AbstractPower getThis(AbstractCreature owner) {
		return owner.powers.stream().filter(p -> p instanceof DeathImprintPower).findAny().orElse(null);
	}
	
	public DeathImprintPower(AbstractCreature owner, int amount) {
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.DEBUFF;
	}

	public void updateDescription() {
		this.description = desc(0) + this.owner.name + desc(1) + this.owner.name + desc(2) + this.amount + desc(3);
	}

	public int onAttacked(DamageInfo info, int damage) {
		if (damage > 0) {
			this.amount += damage < 5 && p().hasRelic("Boot") && info.type == DamageType.NORMAL ? 5 : damage;
			this.updateDescription();
		}
		return damage;
	}

}
