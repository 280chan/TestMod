package testmod.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.RegenPower;

public class HyperRegenerationPower extends AbstractTestPower {
	
	public HyperRegenerationPower(AbstractCreature owner, int amount) {
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = desc(0) + this.amount + desc(1);
	}

	public int onLoseHp(final int damage) {
		if (damage > 0) {
			this.atb(this.apply(this.p(), new RegenPower(this.p(), this.amount)));;
		}
		return damage;
	}

}
