package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

import testmod.mymod.TestMod;
import testmod.powers.AbstractTestPower;

public class AbnormalityKillerUp extends AbstractUpgradedRelic {
	private static final double RATE = 0.25;
	
	private void tryAdd() {
		if (this.hasEnemies()) {
			AbstractDungeon.getMonsters().monsters.stream()
					.filter(m -> m.powers.stream().noneMatch(p -> p instanceof AbnormalityKillerUpPower))
					.forEach(m -> m.powers.add(new AbnormalityKillerUpPower(m)));
			if (AbstractDungeon.getMonsters().monsters.stream().anyMatch(AbnormalityKillerUpPower::anyMatch))
				this.beginLongPulse();
			else
				this.stopPulse();
		}
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.isActive && this.inCombat()) {
			tryAdd();
		}
	}
	
	public void atPreBattle() {
		tryAdd();
	}
	
	public void atTurnStart() {
		tryAdd();
	}
	
	private static class AbnormalityKillerUpPower extends AbstractTestPower
			implements OnReceivePowerPower, InvisiblePower {
		public static boolean anyMatch(AbstractCreature m) {
			return m.powers.stream().anyMatch(p -> !(p instanceof InvisiblePower));
		}
		
		public AbnormalityKillerUpPower(AbstractCreature owner) {
			this.type = PowerType.DEBUFF;
			this.owner = owner;
			this.addMapWithSkip(p -> new AbnormalityKillerUpPower(p.owner));
		}
		
		private double cal(double input) {
			int pAmount = (int) this.owner.powers.stream().filter(p -> !(p instanceof InvisiblePower)).count();
			if (pAmount == 0)
				return input;
			double rate = 1.0 + pAmount * RATE;
			return Math.pow(rate, this.relicStream(AbnormalityKillerUp.class).count()) * input;
		}

		public int onAttacked(DamageInfo info, int damage) {
			return (int) cal(damage);
		}

		@Override
		public boolean onReceivePower(AbstractPower p, AbstractCreature t, AbstractCreature source) {
			if (!(p instanceof InvisiblePower))
				this.relicStream(AbnormalityKillerUp.class).forEach(r -> r.beginLongPulse());
			return true;
		}
	}

}