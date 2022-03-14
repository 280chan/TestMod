package testmod.relics;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;

import testmod.mymod.TestMod;
import testmod.patches.MistCorePatch;

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
	
	public static void changeRoom(MapRoomNode n) {
		n.room = new EventRoom();
	}
	
	public static boolean checkRoom(AbstractRoom r) {
		return r.getClass().isAssignableFrom(MonsterRoom.class);
	}
	
	public static void changeRooms(ArrayList<ArrayList<MapRoomNode>> map, boolean exclude) {
		map.stream().skip(1).flatMap(l -> l.stream()).filter(
				n -> !(exclude && AbstractDungeon.currMapNode.equals(n)) && (n.room == null || checkRoom(n.room)))
				.forEach(MistCore::changeRoom);
	}
	
	private static void changeMultiplayerRooms(ArrayList<ArrayList<MapRoomNode>> map, boolean exclude) {
		if (!Loader.isModLoaded("chronoMods"))
			return;
		map.stream().skip(1).flatMap(l -> l.stream()).filter(n -> !(exclude && AbstractDungeon.currMapNode.equals(n)))
				.forEach(MistCorePatch::modify);
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!this.isActive || AbstractDungeon.map == null || Loader.isModLoaded("FoggyMod"))
			return;
		changeRooms(AbstractDungeon.map, AbstractDungeon.currMapNode != null);
		changeMultiplayerRooms(AbstractDungeon.map, AbstractDungeon.currMapNode != null);
	}

}