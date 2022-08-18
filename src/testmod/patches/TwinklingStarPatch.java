package testmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import testmod.utils.MiscMethods;
import testmod.utils.Star;

public class TwinklingStarPatch implements MiscMethods {
	@SpirePatch(clz = AbstractRelic.class, method = "flash")
	public static class RelicFlashPatch {
		@SpirePostfixPatch
		public static void Postfix(AbstractRelic c) {
			if (!(c instanceof Star) && MISC.p() != null && MISC.p().relics != null) {
				MISC.p().relics.stream().filter(r -> r instanceof Star).forEach(r -> ((Star) r).act());
			}
		}
	}
}
