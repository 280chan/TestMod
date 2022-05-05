package testmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import testmod.relics.TwinklingStar;
import testmod.utils.MiscMethods;

public class TwinklingStarPatch implements MiscMethods {
	@SpirePatch(clz = AbstractRelic.class, method = "flash")
	public static class RelicFlashPatch {
		@SpirePostfixPatch
		public static void Postfix(AbstractRelic c) {
			if ((!(c instanceof TwinklingStar)) && MISC.p() != null && MISC.p().relics != null) {
				MISC.relicStream(TwinklingStar.class).forEach(r -> r.act());
			}
		}
	}
}
