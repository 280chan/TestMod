package patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import relics.TwinklingStar;
import utils.MiscMethods;

public class TwinklingStarPatch implements MiscMethods {
	@SpirePatch(clz = AbstractRelic.class, method = "flash")
	public static class RelicFlashPatch {
		@SpirePostfixPatch
		public static void Postfix(AbstractRelic c) {
			if ((!(c instanceof TwinklingStar)) && INSTANCE.p() != null && INSTANCE.p().relics != null) {
				INSTANCE.relicStream(TwinklingStar.class).forEach(r -> r.act());
			}
		}
	}
}
