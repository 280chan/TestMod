package testmod.patches;

import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.DevConsole;
import basemod.ReflectionHacks;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import testmod.mymod.TestMod;

@SuppressWarnings("rawtypes")
public class SwFPatch {
	public static int inited() {
		TestMod.info("初始化TogetherManagerPatch1类");
		return 1;
	}

	@SpirePatch(cls = "chronoMods.utilities.AntiConsolePrintingPatches$RemoveLogging", method = "patch", paramtypez = {
			Logger.class, String.class }, optional = true)
	public static class StupidSwFPatch {
		public static boolean init = true;
		@SpirePrefixPatch
		public static SpireReturn<Logger> Prefix(Logger __result, String s) {
			return init || TestMod.isLocalTest() ? SpireReturn.Return(__result) : SpireReturn.Continue();
		}
	}
	
	@SpirePatch(cls = "chronoMods.TogetherManager$ConvenienceDebugPresses", method = "Postfix", paramtypez = {
			AbstractDungeon.class }, optional = true)
	public static class TogetherManagerPatch {
		public static int test = inited();
		@SpirePostfixPatch
		public static void Postfix(AbstractDungeon dungeon) {
			DevConsole.enabled = true;
		}
	}
	
	@SpirePatch(cls = "chronoMods.bingo.SendBingoPatches.bingoExhaust", method = "Postfix", optional = true)
	public static class BingoExhaustPatch {
		public static int exhaust = 0;
		
		public static SpireReturn exhaust(boolean act) {
			if (act)
				exhaust++;
			if (AbstractDungeon.player != null && AbstractDungeon.player.exhaustPile.size() + exhaust > 19) {
				try {
					ReflectionHacks.privateStaticMethod(Class.forName("chronoMods.bingo.SendBingoPatches"), "Bingo",
							int.class).invoke(new Object[] {62});
					return SpireReturn.Return();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			return SpireReturn.Continue();
		}
		
		@SpireInsertPatch(locator = Locator.class)
		public static SpireReturn Insert(AbstractDungeon param) {
			return exhaust(false);
		}
		
		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "player");
				int[] raw = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
				return new int[] { raw[0] };
			}
		}
	}
}
