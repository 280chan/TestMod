package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.BlurPower;

import mymod.TestMod;

public class CrystalShield extends MyRelic {
	public static final String ID = "CrystalShield";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "如果你在你的回合获得过 #y格挡 ，该回合结束时获得 #b1 层 #y残影 。";
	
	public CrystalShield() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.RARE, LandingSound.CLINK);
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
			AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new BlurPower(p, 1), 1));
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