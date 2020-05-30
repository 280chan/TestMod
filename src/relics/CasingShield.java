package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import mymod.TestMod;

public class CasingShield extends MyRelic {
	public static final String ID = "CasingShield";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "每当你获得一次 #y格挡 ，本回合接下来获得的 #y格挡 数值都将增加 #b1 。";//遗物效果的文本描叙。
	
	public CasingShield() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}

	public void atPreBattle() {
		if (!this.isActive)
			return;
		this.counter = 0;
	}
	
	public void atTurnStart() {
		if (!this.isActive)
			return;
		this.stopPulse();
		this.counter = 0;
	}
	
	public void onVictory() {
		if (!this.isActive)
			return;
		this.stopPulse();
		this.counter = -1;
	}
	
	public void onEnterRoom(AbstractRoom room) {
		this.onVictory();
	}
	
	public int onPlayerGainedBlock(float amount) {
		if (!this.isActive)
			return super.onPlayerGainedBlock(amount);
		int block = MathUtils.floor(amount) + this.counter;
		if (amount > 0) {
			this.beginLongPulse();
			this.show();
			this.counter++;
		}
		return block;
	}

}