package relics;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.BlurPower;

public class CrystalShield extends AbstractTestRelic {
	public static final String ID = "CrystalShield";
	
	public CrystalShield() {
		super(ID, RelicTier.RARE, LandingSound.CLINK);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void atTurnStart() {
		this.counter = -1;
		this.stopPulse();
    }
	
	public void onPlayerEndTurn() {
		if (this.counter == -2) {
			AbstractPlayer p = AbstractDungeon.player;
			this.addToBot(new ApplyPowerAction(p, p, new BlurPower(p, 1), 1));
			this.stopPulse();
		} else {
			this.counter = -2;
		}
    }
	
	public void onVictory() {
		this.counter = -2;
		this.stopPulse();
    }
	
	public int onPlayerGainedBlock(float blockAmount) {
		int retVal = MathUtils.floor(blockAmount);
		if (retVal > 0) {
			this.counter = -2;
			this.beginLongPulse();
		}
		return retVal;
	}
	
}