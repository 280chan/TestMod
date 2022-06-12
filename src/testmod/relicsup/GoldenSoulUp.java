package testmod.relicsup;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class GoldenSoulUp extends AbstractUpgradedRevivalRelic {
	public static final int RATE = 10;
	
	public GoldenSoulUp() {
		super(RelicTier.BOSS, LandingSound.CLINK);
		this.counter = 0;
	}
	
	public String getUpdatedDescription() {
		if (this.counter > 0) {
			return DESCRIPTIONS[0] + DESCRIPTIONS[1] + (this.counter * RATE + 100) + DESCRIPTIONS[2];
		}
		return DESCRIPTIONS[0];
	}
	
	public void count() {
		this.counter++;
		this.updateDescription();
	}
	
	public void onEquip() {
		if (p().gold > 0) {
			p().increaseMaxHp(p().gold, true);
			p().loseGold(p().gold);
		}
    }
	
	public int onLoseHpLast(int damage) {
		if (damage >= p().currentHealth) {
			if (damage > 2 * p().gold) {
				damage -= 2 * p().gold;
				if (p().gold > 0)
					this.count();
				p().loseGold(p().gold);
				return damage;
			}
			p().loseGold((damage + 1) / 2);
			this.count();
			return 0;
		}
		return damage;
	}
	
	@Override
	protected int damageModifyCheck(AbstractPlayer p, DamageInfo info, int originalDamage) {
		if (p.gold * 2 < originalDamage) {
			if (p.gold > 0) {
				originalDamage -= 2 * p.gold;
				this.count();
				p.loseGold(p.gold);
			}
			return originalDamage;
		} else {
			this.count();
			p.loseGold((originalDamage + 1) / 2);
			return 0;
		}
	}

	@Override
	protected boolean resetHpCheck(AbstractPlayer p, int damageAmount) {
		return true;
	}
	
	public double gainGold(double amount) {
		return amount + amount / 100 * this.counter * RATE;
	}

}