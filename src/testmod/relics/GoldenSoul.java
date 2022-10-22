package testmod.relics;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class GoldenSoul extends AbstractRevivalRelicToModifyDamage {
	public static final int RATE = 10;
	
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
		this.counter = 0;
		p().decreaseMaxHealth(p().maxHealth - Math.max(p().maxHealth / 4, 1));
	}
	
	public int onLoseHpLast(int damage) {
		if (damage >= p().currentHealth) {
			if (damage > p().gold) {
				damage -= p().gold;
				if (p().gold > 0)
					this.count();
				p().loseGold(p().gold);
				return damage;
			}
			p().loseGold(damage);
			this.count();
			return 0;
		}
		return damage;
	}
	
	@Override
	protected int damageModifyCheck(AbstractPlayer p, DamageInfo info, int originalDamage) {
		if (p.gold < originalDamage) {
			if (p.gold > 0) {
				originalDamage -= p.gold;
				this.count();
				p.loseGold(p.gold);
			}
			return originalDamage;
		} else {
			this.count();
			p.loseGold(originalDamage);
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