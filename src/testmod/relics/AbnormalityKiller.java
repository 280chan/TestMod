package testmod.relics;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

import testmod.mymod.TestMod;
import testmod.powers.AbstractTestPower;

public class AbnormalityKiller extends AbstractTestRelic {
	private static final double RATE = 0.25;
	
	private void tryAdd() {
		if (this.hasEnemies()) {
			AbstractDungeon.getMonsters().monsters.stream()
					.filter(m -> m.powers.stream().noneMatch(p -> p instanceof AbnormalityKillerPower))
					.forEach(m -> m.powers.add(new AbnormalityKillerPower(m)));
			if (AbstractDungeon.getMonsters().monsters.stream().anyMatch(AbnormalityKillerPower::anyMatch))
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
	
	private static class AbnormalityKillerPower extends AbstractTestPower
			implements OnReceivePowerPower, InvisiblePower {
		public static boolean anyMatch(AbstractCreature m) {
			return m.powers.stream().anyMatch(p -> !(p instanceof InvisiblePower) && p.type == PowerType.DEBUFF);
		}
		
		public AbnormalityKillerPower(AbstractCreature owner) {
			this.type = PowerType.DEBUFF;
			this.owner = owner;
			this.addMapWithSkip(p -> new AbnormalityKillerPower(p.owner));
		}
		
		private double cal(double input) {
			int pAmount = (int) this.owner.powers.stream()
					.filter(p -> !(p instanceof InvisiblePower) && p.type == PowerType.DEBUFF).count();
			if (pAmount == 0)
				return input;
			double rate = 1.0 + pAmount * RATE;
			return Math.pow(rate, this.relicStream(AbnormalityKiller.class).count()) * input;
		}

		public int onAttacked(DamageInfo info, int damage) {
			return (int) cal(damage);
		}

		@Override
		public boolean onReceivePower(AbstractPower p, AbstractCreature t, AbstractCreature source) {
			if (!(p instanceof InvisiblePower) && p.type == PowerType.DEBUFF)
				this.relicStream(AbnormalityKiller.class).forEach(r -> r.beginLongPulse());
			return true;
		}
	}

}