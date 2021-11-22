package utils;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
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
	public static class GetRelicPatch {
		@SpireInsertPatch(locator = StartLocator.class)
		public static void Start(CardCrawlGame __instance, AbstractPlayer p) {
			RelicGetManager.loading = true;
		}
		@SpireInsertPatch(locator = EndLocator.class)
		public static void End(CardCrawlGame __instance, AbstractPlayer p) {
			RelicGetManager.loading = false;
		}

		private static class StartLocator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctBehavior) throws Exception {
				Matcher.FieldAccessMatcher matcher = new Matcher.FieldAccessMatcher(SaveFile.class, "relics");
				return LineFinder.findInOrder(ctBehavior, matcher);
			}
		}
		private static class EndLocator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctBehavior) throws Exception {
				Matcher.FieldAccessMatcher matcher = new Matcher.FieldAccessMatcher(SaveFile.class, "potions");
				return LineFinder.findInOrder(ctBehavior, matcher);
			}
		}
	}
	
	@SpirePatch(clz = AbstractPlayer.class, method = "reorganizeRelics")
	public static class ReorganizeRelicsPatch {
		@SpirePrefixPatch
		public static void Prefix(AbstractPlayer __instance) {
			RelicGetManager.loading = true;
		}
		@SpirePostfixPatch
		public static void Postfix(AbstractPlayer __instance) {
			RelicGetManager.loading = false;
		}
		
	}
}
