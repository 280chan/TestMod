package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import testmod.relics.Gather;

public class GatherUp extends AbstractUpgradedRelic implements ClickableRelic {
	public static boolean pause = false;
	
	public GatherUp() {
		super(RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public static void trigger() {
		pause = false;
		MISC.relicStream(GatherUp.class).forEach(r -> r.beginLongPulse());
	}
	
	public void onEquip() {
		if (this.inCombat() && !pause)
			trigger();
	}
	
	public void atPreBattle() {
		if (this.isActive)
			trigger();
	}

	@Override
	public void onRightClick() {
		if (this.inCombat() && !pause && Gather.valid()) {
			this.relicStream(GatherUp.class).forEach(r -> r.stopPulse());
			Gather.openScreen(pause = true);
			this.flash();
		}
	}

}