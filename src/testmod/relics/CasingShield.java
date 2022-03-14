package testmod.relics;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class CasingShield extends AbstractTestRelic {
	
	public CasingShield() {
		super(RelicTier.UNCOMMON, LandingSound.MAGICAL);
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