package patches;

import java.util.ArrayList;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import cards.colorless.VirtualReality;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class VirtualRealityPatch {
	@SpirePatch(clz = AbstractCreature.class, method = "addBlock")
	public static class AbstractCreaturePatch {
		@SpireInsertPatch(locator = Locator.class, localvars = { "tmp" })
		public static void Insert(AbstractCreature c, int blockAmount, float tmp) {
			VirtualReality.gainBlock(MathUtils.floor(tmp));
		}
		
		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher finalMatcher = new Matcher.MethodCallMatcher(MathUtils.class, "floor");
				return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
			}
		}
	}
	
	@SpirePatch(clz = AbstractPlayer.class, method = "applyStartOfTurnCards")
	public static class AbstractPlayerPatch {
		public static void Prefix(AbstractPlayer p) {
			VirtualReality.turnStarts();
		}
	}
}
