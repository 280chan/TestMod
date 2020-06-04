package relics;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class Fortitude extends MyRelic{
	
	public static final String ID = "Fortitude";
	
	public Fortitude() {
		super(ID, RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void onPlayerEndTurn() {
		if (!this.isActive)
			return;
		if (AbstractDungeon.player.currentBlock == 0) {
			stopPulse();
			this.show();
			AbstractPlayer p = AbstractDungeon.player;
			this.addToTop(new ApplyPowerAction(p, p, new StrengthPower(p, 3), 3));
		}
	}

	public void atTurnStart() {
		if (!this.isActive)
			return;
		if (AbstractDungeon.player.currentBlock == 0) {
			beginLongPulse();
		}
	}

	public int onPlayerGainedBlock(float blockAmount) {
		if (!this.isActive)
			return MathUtils.floor(blockAmount);
		if (blockAmount > 0.0F) {
			stopPulse();
		}
		return MathUtils.floor(blockAmount);
	}

	public void onVictory() {
		if (!this.isActive)
			return;
		stopPulse();
	}

}