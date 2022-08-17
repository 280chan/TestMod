package testmod.relics;

import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class LifeArmor extends AbstractTestRelic {
	
	public void atPreBattle() {
		this.counter = 0;
		this.beginLongPulse();
	}
	
	public void onEquip() {
		if (this.inCombat())
			this.atPreBattle();
	}
	
	public void onLoseHp(int amount) {
		if (!this.inCombat() || this.counter < 0)
			return;
		p().addBlock(amount + this.counter);
		this.counter++;
	}
	
	public void onVictory() {
		this.counter = -1;
		this.stopPulse();
	}
	
	public void onEnterRoom(AbstractRoom room) {
		this.onVictory();
	}

}