package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;

import mymod.TestMod;

public class Fortitude extends MyRelic{
	
	public static final String ID = "Fortitude";
	public static final String IMG = TestMod.relicIMGPath(ID);
	
	public static final String DESCRIPTION = "如果你在回合结束时没有任何 #y格挡 ，获得 #b3  #y力量 。";//遗物效果的文本描叙。
	
	public Fortitude() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.RARE, LandingSound.MAGICAL);
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
			AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(p, p, new StrengthPower(p, 3), 3));
		}
	}// 触发时机：在玩家回合结束时。

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