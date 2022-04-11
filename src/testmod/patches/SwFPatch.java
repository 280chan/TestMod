package testmod.patches;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.ReflectionHacks;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import testmod.mymod.TestMod;

@SuppressWarnings("rawtypes")
public class SwFPatch {
	@SpirePatch(cls = "chronoMods.TogetherManager", method = "log", optional = true)
	public static class TogetherManagerPatch {
		public static SpireReturn Prefix(String outmessage) {
			return TestMod.spireWithFriendLogger ? SpireReturn.Continue() : SpireReturn.Return(null);
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
