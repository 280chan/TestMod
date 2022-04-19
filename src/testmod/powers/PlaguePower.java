package testmod.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ChokePower;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import com.megacrit.cardcrawl.powers.PoisonPower;

public class PlaguePower extends AbstractTestPower {
	
	public PlaguePower(AbstractCreature owner, int amount) {
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = desc(0) + this.amount + desc(1);
	}
	
	private AbstractPower createPower(int type, AbstractCreature target) {
		switch (type) {
		case 0:
			return new PoisonPower(target, this.owner, this.amount);
		case 1:
			return new ConstrictedPower(target, this.owner, this.amount);
		case 2:
			return new ChokePower(target, this.amount);
		}
		return null;
	}
	
	private void applyPower(int type, AbstractCreature target) {
		this.addToBot(new ApplyPowerAction(target, this.owner, this.createPower(type, target), this.amount, true));
	}
	
	public void atStartOfTurn() {
		for (int i = 0; i < 3; i++) {
			AbstractCreature m = AbstractDungeon.getMonsters().getRandomMonster(null, true,
					AbstractDungeon.cardRandomRng);
			if (m == null)
				return;
			applyPower(i, m);
		}
		this.flashWithoutSound();
    }

}
