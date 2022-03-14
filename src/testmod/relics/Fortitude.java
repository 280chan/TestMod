package testmod.relics;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class Fortitude extends AbstractTestRelic {
	
	public Fortitude() {
		super(RelicTier.UNCOMMON, LandingSound.MAGICAL);
	}
	
	public void onPlayerEndTurn() {
		if (p().currentBlock == 0) {
			stopPulse();
			this.show();
			this.addToTop(apply(p(), new StrengthPower(p(), 3)));
		}
	}

	public void atTurnStart() {
		if (p().currentBlock == 0) {
			beginLongPulse();
		}
	}

	public int onPlayerGainedBlock(float blockAmount) {
		if (blockAmount > 0.0F) {
			stopPulse();
		}
		return MathUtils.floor(blockAmount);
	}

	public void onVictory() {
		stopPulse();
	}

}