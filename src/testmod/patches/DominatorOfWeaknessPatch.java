package testmod.patches;

import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import testmod.relics.DominatorOfWeakness;
import testmod.relicsup.DominatorOfWeaknessUp;
import testmod.utils.MiscMethods;

public class DominatorOfWeaknessPatch {
	private static boolean hasRelic() {
		return amount() + amount1() > 0;
	}
	
	private static int amount() {
		return (int) MiscMethods.MISC.relicStream(DominatorOfWeakness.class).count();
	}
	
	private static int amount1() {
		return (int) MiscMethods.MISC.relicStream(DominatorOfWeaknessUp.class).count();
	}
	
	private static boolean hasFrog(VulnerablePower p) {
		return (p.owner != null) && (!p.owner.isPlayer) && (AbstractDungeon.player.hasRelic("Paper Frog"));
	}
	
	private static boolean hasCrane(WeakPower p) {
		return (!p.owner.isPlayer) && (AbstractDungeon.player.hasRelic("Paper Crane"));
	}
	
	private static double finalRate(double bonus, int powerAmount) {
		return Math.pow(1 + bonus, powerAmount * amount1()) * (1 + bonus * powerAmount * amount());
	}
	
	@SpirePatch(clz = VulnerablePower.class, method = "atDamageReceive")
	public static class VulnerablePowerPatch {
		@SpireInsertPatch(locator = Locator.class)
		public static SpireReturn<Float> Insert(VulnerablePower p, float damage, DamageType type) {
			if (!hasRelic() || p.amount < 2 || p.owner.isPlayer)
				return SpireReturn.Continue();
			return SpireReturn.Return((float) finalRate(hasFrog(p) ? 0.75 : 0.5, p.amount) * damage);
		}
		
		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "player");
				int[] arr = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
				return new int[] { arr[1] };
			}
		}
	}
	
	@SpirePatch(clz = WeakPower.class, method = "atDamageGive")
	public static class WeakPowerPatch {
		@SpireInsertPatch(locator = Locator.class)
		public static SpireReturn<Float> Insert(WeakPower p, float damage, DamageType type) {
			if (!hasRelic() || p.amount < 2 || p.owner.isPlayer)
				return SpireReturn.Continue();
			return SpireReturn
					.Return((float) (damage * Math.pow((hasCrane(p) ? 0.6F : 0.75F), p.amount * amount() + amount1())));
		}
		
		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "player");
				return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
			}
		}
	}
}
