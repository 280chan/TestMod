package relics;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class LifeArmor extends MyRelic {
	public static final String ID = "LifeArmor";

	public LifeArmor() {
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
		if (!this.isActive)
			return;
		this.counter = 0;
		this.beginLongPulse();
	}
	
	public void onLoseHp(int amount) {
		if (!this.isActive || AbstractDungeon.floorNum == 0)
			return;
		if (this.counter < 0)
			return;
		AbstractPlayer p = AbstractDungeon.player;
		p.addBlock(amount + this.counter);
		this.counter++;
	}
	
	public void onVictory() {
		if (!this.isActive)
			return;
		this.counter = -1;
		this.stopPulse();
	}
	
	public void onEnterRoom(AbstractRoom room) {
		this.onVictory();
	}

}