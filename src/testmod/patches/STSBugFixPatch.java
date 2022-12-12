package testmod.patches;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.unique.DualWieldAction;
import com.megacrit.cardcrawl.actions.watcher.OmniscienceAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.relics.RedCirclet;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import testmod.utils.MiscMethods;

public class STSBugFixPatch implements MiscMethods {
	@SpirePatch(clz = RelicLibrary.class, method = "getRelic")
	public static class RedCircletPatch {
		@SpireInsertPatch(locator = Locator.class, localvars = { "key" })
		public static SpireReturn<AbstractRelic> Insert(String key) {
			return "Red Circlet".equals(key) ? SpireReturn.Return(new RedCirclet()) : SpireReturn.Continue();
		}
		
		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher finalMatcher = new Matcher.NewExprMatcher(Circlet.class);
				return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
			}
		}
	}
	
	@SpirePatch(clz = OmniscienceAction.class, method = "update")
	public static class OmnisciencePatch {
		@SpireInsertPatch(locator = Locator.class, localvars = { "c", "tmp" })
		public static void Insert(OmniscienceAction a, AbstractCard c, @ByRef AbstractCard[] tmp) {
			tmp[0] = c.makeSameInstanceOf();
		}

		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "purgeOnUse");
				return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
			}
		}
	}
	
	@SpirePatch(clz = DualWieldAction.class, method = "update")
	public static class DualWieldPatch {
		@SpireInsertPatch(locator = Locator.class, localvars = { "c" })
		public static void Insert(DualWieldAction a, AbstractCard c) {
			AbstractDungeon.actionManager.actions.remove(0);
			MISC.addTmpActionToTop(() -> {
				MISC.p().hand.addToTop(c);
				MISC.p().hand.refreshHandLayout();
			});
		}

		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher finalMatcher = new Matcher.FieldAccessMatcher(HandCardSelectScreen.class, "selectedCards");
				int[] arr = LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
				return new int[] { arr[0] + 2 };
			}
		}
	}
}
