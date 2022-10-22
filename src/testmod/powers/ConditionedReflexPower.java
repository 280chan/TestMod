package testmod.powers;

import java.util.function.Consumer;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MalleablePower;

public class ConditionedReflexPower extends AbstractTestPower {
	private int activeAmount;
	
	public ConditionedReflexPower(AbstractCreature owner, int amount) {
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = desc(0) + this.amount + desc(1);
		 if (this.activeAmount > 0)
			 this.description += desc(2) + this.activeAmount + desc(3);
	}
	
	private void activate(AbstractPower p) {
		Consumer<GainBlockAction> a = p.owner.isPlayer ? this::att : this::atb;
		a.accept(new GainBlockAction(p.owner, p.owner, p.amount));
		p.amount++;
		p.updateDescription();
	}
	
	public void atStartOfTurn() {
		if (this.activeAmount > 0) {
			AbstractPower mp = this.owner.getPower(MalleablePower.POWER_ID);
			if (mp == null)
				return;
			mp.flash();
			this.getNaturalNumberList(this.activeAmount).forEach(a -> this.activate(mp));
			this.activeAmount = 0;
			this.updateDescription();
		}
	}
	
	public int onLoseHp(final int damage) {
		if (damage > 0) {
			this.att(new ApplyPowerAction(owner, owner, new MalleablePower(owner, this.amount), this.amount));
			this.activeAmount++;
			this.updateDescription();
		}
		return damage;
	}

}
