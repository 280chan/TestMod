package testmod.relicsup;

import testmod.relics.Charity;

public class CharityUp extends AbstractUpgradedRelic {
	
	public CharityUp() {
		super(RelicTier.SHOP, LandingSound.MAGICAL);
		this.counter = 0;
	}
	
	public double gainGold(double amount) {
		this.show();
		amount *= (1 + (this.counter++) * 0.01);
		if (!this.isActive)
			return amount;
		p().increaseMaxHp((int) (relicStream(CharityUp.class).count() + relicStream(Charity.class).count()), true);
		return Math.max(0, amount - 1);
	}
}