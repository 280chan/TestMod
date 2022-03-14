package testmod.relics;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

public class GoldenSoul extends AbstractRevivalRelicToModifyDamage {
	public static String ID = "GoldenSoul";
	public static final int RATE = 10;
	
	public GoldenSoul() {
		super(ID, RelicTier.BOSS, LandingSound.CLINK);
		this.setTestTier(BAD);
		this.counter = 0;
	}
	
	public String getUpdatedDescription() {
		if (this.counter > 0) {
			return DESCRIPTIONS[0] + DESCRIPTIONS[1] + (this.counter * RATE + 100) + DESCRIPTIONS[2];
		}
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void count() {
		this.counter++;
		this.updateDescription(p().chosenClass);
	}
	
	public void onEquip() {
		AbstractPlayer p = AbstractDungeon.player;
		p.decreaseMaxHealth(p.maxHealth - Math.max(p.maxHealth / 4, 1));
    }
	
	public int onLoseHpLast(int damage) {
		AbstractPlayer p = AbstractDungeon.player;
		if (damage >= p.currentHealth) {
			if (damage > p.gold) {
				damage -= p.gold;
				if (p.gold > 0)
					this.count();
				p.loseGold(p.gold);
				return damage;
			}
			p.loseGold(damage);
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