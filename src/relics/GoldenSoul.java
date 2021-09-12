package relics;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

public class GoldenSoul extends AbstractRevivalRelicToModifyDamage {
	public static final String ID = "GoldenSoul";
	
	public GoldenSoul() {
		super(ID, RelicTier.BOSS, LandingSound.CLINK);
		this.setTestTier(BAD);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
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
				p.loseGold(p.gold);
				return damage;
			}
			p.loseGold(damage);
			return 0;
		}
		return damage;
	}
	
	@Override
	protected int damageModifyCheck(AbstractPlayer p, DamageInfo info, int originalDamage) {
		if (p.gold < originalDamage) {
			originalDamage -= p.gold;
			p.loseGold(p.gold);
			return originalDamage;
		} else {
			p.loseGold(originalDamage);
			return 0;
		}
	}

	@Override
	protected boolean resetHpCheck(AbstractPlayer p, int damageAmount) {
		return true;
	}

}