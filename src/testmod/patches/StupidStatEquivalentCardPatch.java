package testmod.patches;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class StupidStatEquivalentCardPatch {
	
	@SpirePatch(clz = AbstractCard.class, method = "makeStatEquivalentCopy")
	public static class MakeStatEquivalentCopyPatch {
		private static final String MN = "[Missing Name]";
		private static String tmpName = MN;
		
		@SpirePostfixPatch
		public static AbstractCard Postfix(AbstractCard _return, AbstractCard __instance) {
			_return.magicNumber = __instance.magicNumber;
			_return.exhaust = __instance.exhaust;
			_return.exhaustOnUseOnce = __instance.exhaustOnUseOnce;
			_return.isEthereal = __instance.isEthereal;
			_return.selfRetain = __instance.selfRetain;
			__instance.name = tmpName;
			tmpName = MN;
			return _return;
		}

		private static boolean notBaka() {
			return Stream.of(new Exception().getStackTrace()).noneMatch(
					e -> "RumiaBox.relics.Baka".equals(e.getClassName()) && "update".equals(e.getMethodName()));
		}

		@SpireInsertPatch(locator = Locator.class, localvars = { "card" })
		public static void Insert(AbstractCard __instance, AbstractCard card) {
			tmpName = __instance.name;
			if (notBaka())
				__instance.name = card.name;
		}
		
		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "name");
				int[] tmp = LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
				return new int[] { tmp[0] };
			}
		}
	}
	
}
