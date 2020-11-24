package relics;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class CasingShield extends AbstractTestRelic {
	public static final String ID = "CasingShield";
	
	public CasingShield() {
		super(ID, RelicTier.RARE, LandingSound.MAGICAL);
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
		this.counter = 0;
	}
	
	public void atTurnStart() {
		this.stopPulse();
		this.counter = 0;
	}
	
	public void onVictory() {
		this.stopPulse();
		this.counter = -1;
	}
	
	public void onEnterRoom(AbstractRoom room) {
		this.onVictory();
	}
	
	public int onPlayerGainedBlock(float amount) {
		int block = MathUtils.floor(amount) + this.counter;
		if (amount > 0) {
			this.beginLongPulse();
			this.flash();
			this.counter++;
		}
		return block;
	}

}