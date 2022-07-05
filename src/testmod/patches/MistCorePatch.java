package testmod.patches;

import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.map.RoomTypeAssigner;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoom;

import basemod.ReflectionHacks;
import testmod.relics.MistCore;
import testmod.relicsup.MistCoreUp;
import testmod.utils.MiscMethods;

public class MistCorePatch {
	@SpirePatch(clz = RoomTypeAssigner.class, method = "distributeRoomsAcrossMap")
	public static class RoomTypeAssignerPatch {
		@SpirePostfixPatch
		public static ArrayList<ArrayList<MapRoomNode>> Postfix(Random rng, ArrayList<ArrayList<MapRoomNode>> map,
				ArrayList<AbstractRoom> roomList) {
			if (Loader.isModLoaded("FoggyMod"))
				return map;
			if (MiscMethods.MISC.relicStream(MistCoreUp.class).findAny().isPresent())
				MistCoreUp.changeRooms(map, false);
			else if (MiscMethods.MISC.relicStream(MistCore.class).findAny().isPresent())
				MistCore.changeRooms(map, false);
			return map;
		}
	}
	
	@SpirePatch(cls = "chronoMods.coop.CoopMultiRoom$patchInMultiRooms", method = "Postfix", optional = true)
	public static class CoopMultiRoomPatch {
		@SpirePostfixPatch
		public static void Postfix(MapRoomNode __instance, AbstractRoom room) {
			if (MiscMethods.MISC.relicStream(MistCoreUp.class).findAny().isPresent()) {
				MistCoreUp.rng = new java.util.Random(Settings.seed - AbstractDungeon.actNum);
				modifyUp(__instance);
			} else if (MiscMethods.MISC.relicStream(MistCore.class).findAny().isPresent()) {
				modify(__instance);
			}
		}
	}
	
	public static void modify(MapRoomNode n) {
		Stream.of("second", "third").forEach(s -> checkAndSet(getSpireField(s), n, EventRoom::new));
	}
	
	public static void modifyUp(MapRoomNode n) {
		Stream.of("second", "third").forEach(s -> checkAndSetUp(getSpireField(s), n));
		Stream.of("second", "third").forEach(s -> checkAndSet(getSpireField(s), n, MistCoreUp::getRoom));
	}
	
	private static void checkAndSet(SpireField<AbstractRoom> sf, MapRoomNode n, Supplier<AbstractRoom> r) {
		if (check(sf.get(n)))
			set(sf, n, r);
	}
	
	private static void checkAndSetUp(SpireField<AbstractRoom> sf, MapRoomNode n) {
		if (checkUp(sf.get(n)) && MistCoreUp.rng.nextDouble() < 0.1)
			set(sf, n, TreasureRoom::new);
	}
	
	private static boolean check(AbstractRoom r) {
		return r != null && MistCore.checkRoom(r);
	}
	
	private static boolean checkUp(AbstractRoom r) {
		return r != null && MistCoreUp.checkRoom(r);
	}
	
	private static void set(SpireField<AbstractRoom> sf, MapRoomNode n, Supplier<AbstractRoom> r) {
		sf.set(n, r.get());
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
