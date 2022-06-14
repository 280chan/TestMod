package testmod.relicsup;

import java.util.function.UnaryOperator;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import testmod.powers.AbstractTestPower;

public class ArcanaOfDestinyUp extends AbstractUpgradedRelic {
	
	public ArcanaOfDestinyUp() {
		super(RelicTier.UNCOMMON, LandingSound.SOLID);
	}
	
	private void tryApplyDebuff() {
		if (hasEnemies())
			AbstractDungeon.getMonsters().monsters.stream().filter(ArcanaOfDestinyUpPower::needThis)
					.forEach(ArcanaOfDestinyUpPower::addThis);
	}
	
	public void atPreBattle() {
		tryApplyDebuff();
    }

	public void update() {
		super.update();
		if (this.isActive && this.inCombat())
			tryApplyDebuff();
	}
	
	private void updateHp(int input) {
		if (hasEnemies() && input > 0) {
			this.addTmpActionToTop(() -> {
				tryApplyDebuff();
				AbstractDungeon.getMonsters().monsters.forEach(m -> m.applyPowers());
			});
		}
	}
	
	public void wasHPLost(int damage) {
		this.updateHp(damage);
	}
	
	public int onPlayerHeal(int amount) {
		this.updateHp(amount);
		return amount;
	}

	private static class ArcanaOfDestinyUpPower extends AbstractTestPower implements InvisiblePower {
		
		public static boolean needThis(AbstractCreature m) {
			return m.powers.stream().noneMatch(p -> p instanceof ArcanaOfDestinyUpPower);
		}
		
		public static void addThis(AbstractCreature m) {
			m.powers.add(new ArcanaOfDestinyUpPower(m));
		}
		
		public ArcanaOfDestinyUpPower(AbstractCreature owner) {
			this.owner = owner;
			this.amount = -1;
			updateDescription();
			this.type = PowerType.DEBUFF;
			this.addMap(p -> new ArcanaOfDestinyUpPower(p.owner));
		}
		
		public void updateDescription() {
			 this.description = "";
		}
		
		public void stackPower(final int stackAmount) {
			this.fontScale = 8.0f;
		}
		
		public float HPRate(AbstractCreature p, float f) {
			return p == null || p.maxHealth == 0 ? f : p.currentHealth * 1f / p.maxHealth;
		}
		
		private float damage(float damage) {
			float tmp = HPRate(this.owner, 1f), p = HPRate(p(), 1f);
			return damage * (1 - Math.max(tmp - p, 0.1f));
		}
		
		private float attack(float attack) {
			float tmp = HPRate(this.owner, 0f), p = HPRate(p(), 1f);
			return (attack * (1 + Math.max(3 * (p - tmp), 0.5f)));
		}
		
		private <T> UnaryOperator<T> repeat(UnaryOperator<T> f) {
			return relicStream(ArcanaOfDestinyUp.class).map(r -> f).reduce(t(), this::chain);
		}
		
		public float atDamageGive(float damage, DamageType type) {
			return repeat(this::damage).apply(damage);
		}
		
		public int onAttacked(DamageInfo info, int damage) {
			if (damage > 0) {
				this.addTmpActionToTop(() -> ((AbstractMonster) this.owner).applyPowers());
				return repeat(this::attack).apply(damage * 1f).intValue();
			}
			return 0;
		}
		
		public int onHeal(int amount) {
			if (amount > 0) {
				this.addTmpActionToTop(() -> ((AbstractMonster) this.owner).applyPowers());
			}
			return amount;
		}

	}
	
}