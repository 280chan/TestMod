package relics;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class LifeArmor extends AbstractTestRelic {

	public LifeArmor() {
		super(RelicTier.COMMON, LandingSound.MAGICAL, BAD);
	}
	
	public void atPreBattle() {
		this.counter = 0;
		this.beginLongPulse();
	}
	
	public void onLoseHp(int amount) {
		if (AbstractDungeon.floorNum == 0)
			return;
		if (this.counter < 0)
			return;
		AbstractPlayer p = AbstractDungeon.player;
		p.addBlock(amount + this.counter);
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