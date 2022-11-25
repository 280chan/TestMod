package testmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EmptyRoom;

@SpirePatch(clz = AbstractDungeon.class, method = "getCurrRoom")
public class StupidGetCurrRoomPatch {
	@SpirePrefixPatch
	public static SpireReturn<AbstractRoom> Prefix() {
		return AbstractDungeon.currMapNode == null ? SpireReturn.Return(new EmptyRoom()) : SpireReturn.Continue();
	}
}
