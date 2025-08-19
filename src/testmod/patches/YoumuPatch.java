package testmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class YoumuPatch {

	@SpirePatch(cls = "patches_YM.YoumuPatch$hasRelicResurrect", method = "Insert", paramtypez = {
			AbstractPlayer.class, String.class }, optional = true)
	public static class RewriteYoumuPatch {
		@SpirePrefixPatch
		public static SpireReturn<SpireReturn<Boolean>> Prefix(AbstractPlayer p, String targetID) {
			if ("Youmu".equals(p.name) && ("Blue Candle".equals(targetID) || "Medical Kit".equals(targetID)))
				return SpireReturn.Return(SpireReturn.Return(true));
			return SpireReturn.Return(SpireReturn.Continue());
		}
	}
}
