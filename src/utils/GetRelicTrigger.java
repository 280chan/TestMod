package utils;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import javassist.CtBehavior;

public interface GetRelicTrigger {
	void receiveRelicGet(AbstractRelic r);
	
	public static class RelicGetManager {
		public static boolean loading = false;
	}
	
	@SpirePatch(clz = CardCrawlGame.class, method = "loadPlayerSave")
	public static class StartGetRelicPatch {
		@SpireInsertPatch(locator = Locator.class)
		public static void Insert(CardCrawlGame __instance, AbstractPlayer p) {
			RelicGetManager.loading = true;
		}

		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctBehavior) throws Exception {
				Matcher.FieldAccessMatcher matcher = new Matcher.FieldAccessMatcher(SaveFile.class, "relics");
				return LineFinder.findInOrder(ctBehavior, matcher);
			}
		}
	}
	
	@SpirePatch(clz = CardCrawlGame.class, method = "loadPlayerSave")
	public static class EndGetRelicPatch {
		@SpireInsertPatch(locator = Locator.class)
		public static void Insert(CardCrawlGame __instance, AbstractPlayer p) {
			RelicGetManager.loading = false;
		}
		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctBehavior) throws Exception {
				Matcher.FieldAccessMatcher matcher = new Matcher.FieldAccessMatcher(SaveFile.class, "potions");
				return LineFinder.findInOrder(ctBehavior, matcher);
			}
		}
	}
}
