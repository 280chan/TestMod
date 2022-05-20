package testmod.relicsup;

public class BalancedPeriaptUp extends AbstractUpgradedRelic {
	
	public BalancedPeriaptUp() {
		super(RelicTier.UNCOMMON, LandingSound.FLAT);
	}
	
	private float modify(float input) {
		return input * 2;
	}
	
	public float preChangeMaxHP(float amount) {
		if (!this.isActive || p().isDead || p().isDying || amount == 0)
			return amount;
		if (amount < 0)
			return Math.abs(amount);
		return relicStream(BalancedPeriaptUp.class).map(r -> get(this::modify)).reduce(t(), this::chain).apply(amount);
    }
	
}