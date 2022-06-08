package testmod.relicsup;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.unique.LimitBreakAction;
import com.megacrit.cardcrawl.powers.StrengthPower;

import testmod.relics.Fortitude;

public class FortitudeUp extends AbstractUpgradedRelic {
	
	public FortitudeUp() {
		super(RelicTier.UNCOMMON, LandingSound.MAGICAL);
	}
	
	public void onPlayerEndTurn() {
		if (p().currentBlock == 0) {
			if (this.isActive) {
				this.relicStream(Fortitude.class).forEach(Fortitude::act);
			}
			stopPulse();
			this.show();
			this.addTmpActionToBot(this::act);
		} else {
			this.atb(apply(p(), new StrengthPower(p(), 1)));
		}
	}
	
	private void act() {
		this.att(p().powers.stream().anyMatch(p -> p instanceof StrengthPower && p.amount > 0) ? new LimitBreakAction()
				: apply(p(), new StrengthPower(p(), 3)));
	}

	public void atTurnStart() {
		if (p().currentBlock == 0)
			beginLongPulse();
	}

	public int onPlayerGainedBlock(float blockAmount) {
		if (blockAmount > 0.0F)
			stopPulse();
		return MathUtils.floor(blockAmount);
	}

	public void onVictory() {
		stopPulse();
	}

}