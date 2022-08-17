package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class LifeArmorUp extends AbstractUpgradedRelic {
	
	public void atPreBattle() {
		this.beginLongPulse();
	}
	
	public void onEquip() {
		if (this.inCombat())
			this.atPreBattle();
		this.counter = 0;
	}
	
	public void onLoseHp(int amount) {
		if (!this.inCombat())
			return;
		this.att(new AddTemporaryHPAction(p(), p(), amount + this.counter));
		this.counter++;
	}
	
	public void onVictory() {
		this.stopPulse();
	}
	
	public void onEnterRoom(AbstractRoom room) {
		this.onVictory();
	}

}