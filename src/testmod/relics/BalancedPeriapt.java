package testmod.relics;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;

import testmod.relicsup.BalancedPeriaptUp;
import testmod.relicsup.NineUp;

public class BalancedPeriapt extends AbstractTestRelic {
	
	public BalancedPeriapt() {
		super(RelicTier.UNCOMMON, LandingSound.FLAT, BAD);
	}
	
	private float modify(float input) {
		return input * 3;
	}
	
	public float preChangeMaxHP(float amount) {
		if (!this.isActive || p().isDead || p().isDying || relicStream(BalancedPeriaptUp.class).count() > 0)
			return amount;
		if (NineUp.acting)
			return amount;
		if (amount < 0) {
			p().damage(new DamageInfo(p(), (int)(-amount), DamageType.HP_LOSS));
		} else if (amount > 0) {
			p().heal(relicStream(BalancedPeriapt.class).map(r -> get(this::modify)).reduce(t(), this::chain)
					.apply(amount).intValue());
			this.show();
		}
    	return 0;
    }
	
}