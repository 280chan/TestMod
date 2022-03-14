package testmod.relics;

import testmod.powers.JusticePower;

public class Justice extends AbstractTestRelic {
	
	public Justice() {
		super(RelicTier.UNCOMMON, LandingSound.SOLID);
	}
	
	public void atPreBattle() {
		if (!isActive)
			return;
		p().powers.add(new JusticePower(p()));
    }
	
	public void atTurnStart() {
		if (!isActive)
			return;
		if (!JusticePower.hasThis(p()))
			this.addTmpActionToTop(() -> p().powers.add(new JusticePower(p())));
    }
}