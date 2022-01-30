package patches;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.shop.ShopScreen;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import relics.RetroFilter;

public class RetroFilterPatch {
	@SpirePatch(clz = ShopScreen.class, method = "purchaseCard")
	public static class ShopScreenPatch {
		@SpireInsertPatch(locator = Locator.class, localvars = { "c" })
		public static void Insert(ShopScreen ss, AbstractCard hoveredCard, AbstractCard c) {
			RetroFilter.getThis().forEach(r -> r.onPreviewObtainCard(c));
		}
		
		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "set");
				int[] raw = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
				return new int[] { raw[0] };
			}
		}
	}
}
