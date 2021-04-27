package patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;

import mymod.TestMod;

@SuppressWarnings("rawtypes")
public class SwFLoggerPatch {
	@SpirePatch(cls = "chronoMods.TogetherManager", method = "log", optional = true)
	public static class TogetherManagerPatch {
		public static SpireReturn Prefix(String outmessage) {
			return TestMod.spireWithFriendLogger ? SpireReturn.Continue() : SpireReturn.Return(null);
		}
	}
}
