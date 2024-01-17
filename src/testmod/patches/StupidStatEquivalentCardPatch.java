package testmod.patches;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.SearingBlow;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import testmod.utils.MiscMethods;

public class StupidStatEquivalentCardPatch implements MiscMethods {

	@SpirePatch(clz = CardModifierManager.class, method = "lambda$copyModifiers$5")
	public static class LoadoutUnexhaustModPatch {
		private static boolean loadoutUnexhaustTriggered = false;
		
		@SpireInsertPatch(locator = Locator.class)
		public static void Insert(boolean removeOld, final AbstractCard oldCard, final AbstractCard newCard,
				AbstractCardModifier mod) {
			if (MakeStatEquivalentCopyPatch.prefixStart) {
				try {
					loadoutUnexhaustTriggered |= Class.forName("loadout.cardmods.UnexhaustMod") == mod.getClass();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				MakeStatEquivalentCopyPatch.prefixStart = false;
			}
		}
		
		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher finalMatcher = new Matcher.MethodCallMatcher(CardModifierManager.class, "modifiers");
				return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
			}
		}
	}
	
	@SpirePatch(clz = AbstractCard.class, method = "makeStatEquivalentCopy")
	public static class MakeStatEquivalentCopyPatch {
		private static final String MN = "[Missing Name]";
		private static String tmpName = MN;
		private static boolean prefixStart = false;
		
		@SpirePrefixPatch
		public static void Prefix(AbstractCard __instance) {
			prefixStart = Loader.isModLoaded("loadout");
		}
		
		@SpirePostfixPatch
		public static AbstractCard Postfix(AbstractCard _return, AbstractCard __instance) {
			_return.magicNumber = __instance.magicNumber;
			if (!LoadoutUnexhaustModPatch.loadoutUnexhaustTriggered)
				_return.exhaust = __instance.exhaust;
			_return.exhaustOnUseOnce = __instance.exhaustOnUseOnce;
			_return.isEthereal = __instance.isEthereal;
			_return.selfRetain = __instance.selfRetain;
			__instance.name = tmpName;
			tmpName = MN;
			LoadoutUnexhaustModPatch.loadoutUnexhaustTriggered = false;
			return _return;
		}

		@SpireInsertPatch(locator = Locator.class, localvars = { "card" })
		public static void Insert(AbstractCard __instance, AbstractCard card) {
			tmpName = __instance.name;
			if (!MISC.hasStack("RumiaBox.relics.Baka", "update"))
				__instance.name = card.name;
		}
		
		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "name");
				return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
			}
		}
	}

	@SpirePatch(clz = SearingBlow.class, method = "makeCopy")
	public static class SearingBlowMakeCopyPatch {
		@SpirePrefixPatch
		public static SpireReturn<AbstractCard> Prefix(SearingBlow c) {
			return SpireReturn.Return(new SearingBlow());
		}
	}
	
}
