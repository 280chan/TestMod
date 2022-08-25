package testmod.relics;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EmptyRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.rooms.TrueVictoryRoom;
import com.megacrit.cardcrawl.rooms.VictoryRoom;
import com.megacrit.cardcrawl.vfx.FadeWipeParticle;

import testmod.relicsup.PortablePortalUp;

public class PortablePortal extends AbstractTestRelic {
	public static boolean acting = false;
	
	public void onEquip() {
		this.counter = -2;
		this.addEnergy();
    }
	
	public void onUnequip() {
		this.reduceEnergy();
    }
	
	public boolean canSpawn() {
		return AbstractDungeon.floorNum > 0;
	}
	
	public void onEnterRoom(final AbstractRoom room) {
		if (relicStream(PortablePortalUp.class).count() > 0 || room instanceof NeowRoom || room instanceof EmptyRoom
				|| room instanceof TreasureRoomBoss || room instanceof VictoryRoom || room instanceof TrueVictoryRoom
				|| room instanceof MonsterRoomBoss) {
			return;
		}
		if (this.counter == -2 && !acting) {
			this.counter = -3;
			acting = true;
			this.show();
			room.phase = AbstractRoom.RoomPhase.COMPLETE;
			MapRoomNode node = new MapRoomNode(-1, 15);
	        node.room = new MonsterRoomBoss();
	        AbstractDungeon.nextRoom = node;
	        CardCrawlGame.music.fadeOutTempBGM();
	        AbstractDungeon.pathX.add(Integer.valueOf(1));
	        AbstractDungeon.pathY.add(Integer.valueOf(15));
	        AbstractDungeon.topLevelEffects.add(new FadeWipeParticle());
	        AbstractDungeon.nextRoomTransitionStart();
		} else if (this.isActive && acting) {
			acting = false;
		}
    }

}