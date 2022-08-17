package testmod.relicsup;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoom;

import testmod.mymod.TestMod;
import testmod.patches.MistCorePatch;
import testmod.relics.MistCore;

public class MistCoreUp extends AbstractUpgradedRelic {
	public static Random rng;
	
	public void onEnterRoom(final AbstractRoom room) {
		if (room instanceof EventRoom) {
			p().increaseMaxHp(Math.max(1, p().maxHealth / 20), true);
			this.flash();
		}
    }
	
	public static AbstractRoom getRoom() {
		if (rng == null)
			rng = new Random(Settings.seed - AbstractDungeon.actNum);
		return rng.nextDouble() < 0.1 ? new TreasureRoom() : new EventRoom();
	}
	
	public static void setTreasureRoom(MapRoomNode n) {
		if (rng.nextDouble() < 0.1)
			n.room = new TreasureRoom();
	}
	
	public static void changeRoom(MapRoomNode n) {
		n.room = new EventRoom();
	}
	
	public static boolean checkRoom(AbstractRoom r) {
		return r instanceof EventRoom;
	}
	
	private static Stream<MapRoomNode> stream(ArrayList<ArrayList<MapRoomNode>> map, boolean exclude) {
		return map.stream().skip(1).flatMap(l -> l.stream())
				.filter(n -> !(exclude && AbstractDungeon.currMapNode.equals(n)));
	}
	
	public static void changeRooms(ArrayList<ArrayList<MapRoomNode>> map, boolean exclude) {
		rng = new Random(Settings.seed - AbstractDungeon.actNum);
		stream(map, exclude).filter(n -> n.room == null || MistCore.checkRoom(n.room)).forEach(MistCoreUp::changeRoom);
		stream(map, exclude).filter(n -> n.room == null || checkRoom(n.room)).forEach(MistCoreUp::setTreasureRoom);
	}
	
	public static void changeMultiplayerRooms(ArrayList<ArrayList<MapRoomNode>> map, boolean exclude) {
		if (!Loader.isModLoaded("chronoMods"))
			return;
		stream(map, exclude).forEach(MistCorePatch::modifyUp);
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!this.isActive || AbstractDungeon.map == null || Loader.isModLoaded("FoggyMod"))
			return;
		rng = null;
		changeRooms(AbstractDungeon.map, AbstractDungeon.currMapNode != null);
		changeMultiplayerRooms(AbstractDungeon.map, AbstractDungeon.currMapNode != null);
	}

}