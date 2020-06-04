package relics;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.BlurPower;

public class CrystalShield extends MyRelic {
	public static final String ID = "CrystalShield";
	
	public CrystalShield() {
		super(ID, RelicTier.RARE, LandingSound.CLINK);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void atTurnStart() {
		if (!this.isActive)
			return;
		this.counter = -1;
		this.stopPulse();
    }
	
	public void onPlayerEndTurn() {
		if (!this.isActive)
			return;
		if (this.counter == -2) {
			AbstractPlayer p = AbstractDungeon.player;
			this.addToBot(new ApplyPowerAction(p, p, new BlurPower(p, 1), 1));
			this.stopPulse();
		} else {
			this.counter = -2;
		}
    }
	
	public void onVictory() {
		if (!this.isActive)
			return;
		this.counter = -2;
		this.stopPulse();
    }
	
	public int onPlayerGainedBlock(float blockAmount) {
		int retVal = MathUtils.floor(blockAmount);
		if (this.isActive && retVal > 0) {
			this.counter = -2;
			this.beginLongPulse();
		}
		return retVal;
	}
	
}