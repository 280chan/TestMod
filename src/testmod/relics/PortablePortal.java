package testmod.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.rooms.TrueVictoryRoom;
import com.megacrit.cardcrawl.rooms.VictoryRoom;
import com.megacrit.cardcrawl.vfx.FadeWipeParticle;

public class PortablePortal extends AbstractTestRelic implements ClickableRelic {
	
	public void onEquip() {
		this.counter = -2;
    }
	
	public void onUnequip() {
		if (this.counter == -3)
			this.reduceEnergy();
    }
	
	public void justEnteredRoom(AbstractRoom r) {
		if (!(r instanceof MonsterRoom) && this.check(r))
			this.beginLongPulse();
		else
			this.stopPulse();
	}
	
	public void atPreBattle() {
		this.stopPulse();
	}
	
	public void onVictory() {
		if (this.check())
			this.beginLongPulse();
	}
	
	private boolean check() {
		return this.check(AbstractDungeon.currMapNode == null ? null : AbstractDungeon.getCurrRoom());
	}
	
	private boolean check(AbstractRoom r) {
		if (r instanceof TreasureRoomBoss || r instanceof VictoryRoom || r instanceof TrueVictoryRoom
				|| r instanceof MonsterRoomBoss || r instanceof RestRoom) {
			return false;
		}
		return this.counter == -2;
	}

	@Override
	public void onRightClick() {
		AbstractRoom room = AbstractDungeon.currMapNode == null ? null: AbstractDungeon.getCurrRoom();
		if (this.check(room) && !this.inCombat()) {
			this.addEnergy();
			this.counter = -3;
			this.show();
			if (room != null)
				room.phase = AbstractRoom.RoomPhase.COMPLETE;
			AbstractDungeon.nextRoom = new MapRoomNode(-1, 15);
			AbstractDungeon.nextRoom.room = new MonsterRoomBoss();
	        CardCrawlGame.music.fadeOutTempBGM();
	        AbstractDungeon.pathX.add(Integer.valueOf(1));
	        AbstractDungeon.pathY.add(Integer.valueOf(15));
	        AbstractDungeon.topLevelEffects.add(new FadeWipeParticle());
	        AbstractDungeon.nextRoomTransitionStart();
		}
	}

}