package relics;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import mymod.TestMod;

public class MistCore extends AbstractTestRelic {
	
	public MistCore() {
		super(RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public void onEnterRoom(final AbstractRoom room) {
		if (room instanceof EventRoom)
			p().increaseMaxHp(1, true);
		else
			p().decreaseMaxHealth(1);
		this.flash();
    }
	
	public static void changeRooms(ArrayList<ArrayList<MapRoomNode>> map, boolean exclude) {
		map.stream().skip(1).flatMap(l -> l.stream())
				.filter(n -> !(exclude && AbstractDungeon.currMapNode.equals(n))
						&& (n.room == null || n.room.getClass().isAssignableFrom(MonsterRoom.class)))
				.forEach(n -> n.room = new EventRoom());
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!this.isActive || AbstractDungeon.map == null || Loader.isModLoaded("FoggyMod"))
			return;
		changeRooms(AbstractDungeon.map, AbstractDungeon.currMapNode != null);
	}

}