package testmod.relicsup;

import testmod.relics.TwinklingStar;
import testmod.relics.TwinklingStar.TwinklingStarDamageAction;
import testmod.utils.CounterKeeper;
import testmod.utils.Star;

public class TwinklingStarUp extends AbstractUpgradedRelic implements Star, CounterKeeper {
	public static final int STEP = 200;
	
	public TwinklingStarUp() {
		this.counter = 0;
	}
	
	public void onVictory() {
		if (this.isActive && TwinklingStar.lock)
			TwinklingStar.lock = false;
		TwinklingStar.clearTheFatherAction();
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0] + f() + DESCRIPTIONS[1];
	}
	
	private int f() {
		return this.counter > (31 * STEP - 1) ? 2000000000 : 1 << (this.counter / STEP);
	}
	
	public void act() {
		if (this.hasEnemies() && !TwinklingStar.lock && !TwinklingStar.checkTheFatherAction()) {
			this.counter++;
			if (this.counter % STEP == STEP - 1) {
				this.beginLongPulse();
			} else if (this.counter % STEP == 0) {
				this.stopPulse();
				this.updateDescription();
				this.addRandomKey();
			}
			this.flash();
			this.atb(new TwinklingStarDamageAction(f()));
		}
	}
	
}