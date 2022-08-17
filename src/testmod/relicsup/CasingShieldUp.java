package testmod.relicsup;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class CasingShieldUp extends AbstractUpgradedRelic {

	public void atPreBattle() {
		this.counter = 0;
	}
	
	public void atTurnStart() {
		if ((this.counter /= 2) <= 0)
			this.stopPulse();
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