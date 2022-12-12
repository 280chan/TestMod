package testmod.patches;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.AbstractCreature;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import testmod.relics.ShadowAmulet;
import testmod.relicsup.ShadowAmuletUp;

@SpirePatch(clz = AbstractCreature.class, method = "loseBlock", paramtypes = {"int", "boolean"})
public class ShadowAmuletPatch {
	@SpireInsertPatch(locator = Locator.class)
	public static void Insert(AbstractCreature c, int amount, boolean noAnimation) {
		if (c.isPlayer) {
			ShadowAmulet.onLoseBlock(Math.min(amount, c.currentBlock));
			ShadowAmuletUp.onLoseBlock(Math.min(amount, c.currentBlock));
		}
	}
	
	private static class Locator extends SpireInsertLocator {
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
			Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCreature.class, "currentBlock");
			int[] arr = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
			return new int[] { arr[1] };
		}
	}
}
