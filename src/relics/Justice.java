package relics;

import powers.JusticePower;

public class Justice extends AbstractTestRelic {
	
	public Justice() {
		super(RelicTier.UNCOMMON, LandingSound.SOLID);
	}
	
	public void atPreBattle() {
		if (!isActive)
			return;
		this.addToTop(apply(p(), new JusticePower(p(), this)));
    }
	
	public void atTurnStart() {
		if (!isActive)
			return;
		if (!JusticePower.hasThis(p()))
			this.addToTop(apply(p(), new JusticePower(p(), this)));
    }
}