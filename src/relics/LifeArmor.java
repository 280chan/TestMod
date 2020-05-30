package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import mymod.TestMod;

public class LifeArmor extends MyRelic {
	public static final String ID = "LifeArmor";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "在战斗中失去生命后，立即获得其数值的 #y格挡 ，每失去一次，下次额外获得 #b1 点。";

	public LifeArmor() {
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