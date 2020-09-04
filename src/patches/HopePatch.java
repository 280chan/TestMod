package patches;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import mymod.TestMod;
import relics.Hope;

public class HopePatch {
	@SpirePatch(clz = GameActionManager.class, method = "getNextAction")
	public static class GameActionManagerPatch {
		@SpireInsertPatch(locator = Locator.class)
		public static void Insert(GameActionManager gam) {
			Hope hope = (Hope) AbstractDungeon.player.getRelic(TestMod.makeID(Hope.ID));
			if (hope != null)
				hope.disableUntilTurnEnds();
		}
		
		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "getRelic");
				return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
			}
		}
	}
}
