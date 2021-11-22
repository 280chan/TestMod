package patches;

import java.util.ArrayList;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.map.RoomTypeAssigner;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import relics.MistCore;
import utils.MiscMethods;

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
}
