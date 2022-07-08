package testmod.relicsup;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class NineUp extends AbstractUpgradedRevivalRelic {
	
	public static boolean acting = false;
	
	public NineUp() {
		super(RelicTier.SHOP, LandingSound.MAGICAL);
	}
	
	private boolean check9(int damage) {
		if (damage % 9 == 0)
			return true;
		boolean block = false;
		while (damage > 0 && !block) {
			block = damage % 10 == 9;
			damage /= 10;
		}
		return block;
	}
	
	public int onAttacked(final DamageInfo info, final int damage) {
		if (check9(damage) || damage == p().currentHealth) {
			return 0;
		}
		if (damage > p().currentHealth) {
			int pre = p().maxHealth;
			acting = true;
			p().decreaseMaxHealth(9);
			acting = false;
			return pre > 9 || p().maxHealth > 9 ? 0 : 1;
		}
		return damage;
    }

	@Override
	protected int damageModifyCheck(AbstractPlayer p, DamageInfo info, int originalDamage) {
		return check9(originalDamage) || p().maxHealth > 9 ? 0 : 1;
	}

	@Override
	protected boolean resetHpCheck(AbstractPlayer p, int damageAmount) {
		return true;
	}
	
}