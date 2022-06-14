package testmod.relicsup;

import java.util.function.Consumer;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import testmod.powers.AbstractTestPower;

public class HarvestTotemUp extends AbstractUpgradedRelic {
	
	public HarvestTotemUp() {
		super(RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public void onEquip() {
		p().increaseMaxHp(Math.max(1, p().maxHealth), false);
    }
	
	public int onPlayerHeal(int amount) {
		return amount > 0 ? Math.max(0, Math.min(3 * amount, Integer.MAX_VALUE - p().currentHealth)) : 0;
    }
	
	public float preChangeMaxHP(float amount) {
		return amount >= 0 ? 3 * amount : -amount;
	}

	public static int count() {
		return (int) MISC.relicStream(HarvestTotemUp.class).count();
	}
	
	private void tryApplyDebuff() {
		if (this.isActive && hasEnemies() && count() > 0)
			AbstractDungeon.getMonsters().monsters.stream().filter(this::notHave).forEach(this::addThis);
	}
	
	private boolean notHave(AbstractCreature m) {
		return m.powers.stream().noneMatch(p -> p instanceof HarvestTotemPowerUp);
	}
	
	private void addThis(AbstractCreature m) {
		m.powers.add(new HarvestTotemPowerUp(m));
	}

	public void atPreBattle() {
		tryApplyDebuff();
    }
	
	public void update() {
		super.update();
		if (this.isActive && this.inCombat())
			tryApplyDebuff();
	}

	private static class HarvestTotemPowerUp extends AbstractTestPower implements InvisiblePower {
		public HarvestTotemPowerUp(AbstractCreature owner) {
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

		private float finalDamage(float dmg, Consumer<HarvestTotemUp> f) {
			return relicStream(HarvestTotemUp.class).peek(f).map(r -> get(this::g)).reduce(t(), this::chain).apply(dmg);
		}

		private float g(float input) {
			float tmp = input * (p().maxHealth > this.owner.maxHealth ? p().maxHealth * 1f / this.owner.maxHealth : 1f);
			return tmp > Integer.MAX_VALUE || tmp < -1 ? Integer.MAX_VALUE : tmp;
		}
		
		private boolean check() {
			return this.owner.maxHealth > 0 && p().maxHealth > this.owner.maxHealth;
		}

		public float atDamageFinalReceive(float damage, DamageType type) {
			return damage > 0 && type == DamageType.NORMAL && check() ? finalDamage(damage, empty()) : damage;
		}

		public int onAttacked(DamageInfo info, int dmg) {
			if (this.owner.maxHealth < 1 && dmg > 0 && count() > 0)
				return Integer.MAX_VALUE;
			return dmg > 0 && check() && info.type != DamageType.NORMAL ? (int) finalDamage(dmg, r -> r.show()) : dmg;
		}
	}
	
}