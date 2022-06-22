package testmod.relicsup;

import java.util.function.Consumer;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import testmod.powers.AbstractTestPower;

public class GiantKillerUp extends AbstractUpgradedRelic {
	
	public GiantKillerUp() {
		super(RelicTier.RARE, LandingSound.HEAVY);
	}

	public static int count() {
		return (int) MISC.relicStream(GiantKillerUp.class).count();
	}
	
	private void tryApplyDebuff() {
		if (this.isActive && hasEnemies() && count() > 0)
			AbstractDungeon.getMonsters().monsters.stream().filter(this::notHave).forEach(this::addThis);
	}
	
	private boolean notHave(AbstractCreature m) {
		return m.powers.stream().noneMatch(p -> p instanceof GiantKillerPowerUp);
	}
	
	private void addThis(AbstractCreature m) {
		m.powers.add(new GiantKillerPowerUp(m));
	}

	public void atPreBattle() {
		tryApplyDebuff();
    }

	public void update() {
		super.update();
		if (this.isActive && this.inCombat())
			tryApplyDebuff();
	}

	private static class GiantKillerPowerUp extends AbstractTestPower implements InvisiblePower {
		public GiantKillerPowerUp(AbstractCreature owner) {
			this.owner = owner;
			this.amount = -1;
			updateDescription();
			this.type = PowerType.DEBUFF;
			this.priority = -10000;
		}

		public void updateDescription() {
			this.description = "";
		}

		public void stackPower(final int stackAmount) {
			this.fontScale = 8.0f;
		}

		private float finalDamage(float dmg, Consumer<GiantKillerUp> f) {
			return relicStream(GiantKillerUp.class).peek(f).map(r -> get(this::g)).reduce(t(), this::chain).apply(dmg);
		}
		
		private float rate(AbstractCreature a, AbstractCreature b) {
			return a.maxHealth > b.currentHealth ? a.maxHealth * 1f / b.currentHealth : 1f;
		}

		private float g(float input) {
			float tmp = input * rate(p(), this.owner) * rate(this.owner, p());
			return tmp > Integer.MAX_VALUE || tmp < -1 ? Integer.MAX_VALUE : tmp;
		}
		
		private boolean check() {
			return p().currentHealth > 0 && this.owner.currentHealth > 0
					&& (p().currentHealth < this.owner.maxHealth || p().maxHealth > this.owner.currentHealth);
		}

		public float atDamageFinalReceive(float damage, DamageType type) {
			return damage > 0 && type == DamageType.NORMAL && check() ? finalDamage(damage, empty()) : damage;
		}

		public int onAttacked(DamageInfo info, int dmg) {
			if ((p().currentHealth < 1 || this.owner.currentHealth < 1) && dmg > 0 && count() > 0)
				return Integer.MAX_VALUE;
			return dmg > 0 && check() && info.type != DamageType.NORMAL ? (int) finalDamage(dmg, r -> r.show()) : dmg;
		}
	}
}