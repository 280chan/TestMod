package relics;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class Fortitude extends AbstractTestRelic{
	
	public static final String ID = "Fortitude";
	
	public Fortitude() {
		super(ID, RelicTier.UNCOMMON, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void onPlayerEndTurn() {
		if (AbstractDungeon.player.currentBlock == 0) {
			stopPulse();
			this.show();
			AbstractPlayer p = AbstractDungeon.player;
			this.addToTop(new ApplyPowerAction(p, p, new StrengthPower(p, 3), 3));
		}
	}

	public void atTurnStart() {
		if (AbstractDungeon.player.currentBlock == 0) {
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