package testmod.patches;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.map.RoomTypeAssigner;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;

import basemod.ReflectionHacks;
import testmod.relics.MistCore;
import testmod.utils.MiscMethods;

public class MistCorePatch {
	@SpirePatch(clz = RoomTypeAssigner.class, method = "distributeRoomsAcrossMap")
	public static class RoomTypeAssignerPatch {
		@SpirePostfixPatch
		public static ArrayList<ArrayList<MapRoomNode>> Postfix(Random rng, ArrayList<ArrayList<MapRoomNode>> map,
				ArrayList<AbstractRoom> roomList) {
			if (Loader.isModLoaded("FoggyMod"))
				return map;
			if (MiscMethods.INSTANCE.relicStream(MistCore.class).findAny().isPresent())
				MistCore.changeRooms(map, false);
			return map;
		}
	}
	
	@SpirePatch(cls = "chronoMods.coop.CoopMultiRoom$patchInMultiRooms", method = "Postfix", optional = true)
	public static class CoopMultiRoomPatch {
		@SpirePostfixPatch
		public static void Postfix(MapRoomNode __instance, AbstractRoom room) {
			if (MiscMethods.INSTANCE.relicStream(MistCore.class).findAny().isPresent()) {
				modify(__instance);
			}
		}
	}
	
	public static void modify(MapRoomNode n) {
		Stream.of("second", "third").forEach(s -> checkAndSet(getSpireField(s), n));
	}
	
	private static void checkAndSet(SpireField<AbstractRoom> sf, MapRoomNode n) {
		if (check(sf.get(n)))
			set(sf, n);
	}
	
	private static boolean check(AbstractRoom r) {
		return r != null && MistCore.checkRoom(r);
	}
	
	private static void set(SpireField<AbstractRoom> sf, MapRoomNode n) {
		sf.set(n, new EventRoom());
	}
	
	private static SpireField<AbstractRoom> getSpireField(String prefix) {
		String className = "chronoMods.coop.CoopMultiRoom$" + prefix + "RoomField";
		try {
			return ReflectionHacks.getPrivateStatic(Class.forName(className), prefix + "Room");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
