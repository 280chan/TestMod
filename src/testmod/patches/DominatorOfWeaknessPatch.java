package testmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
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
		return Math.pow(1 + bonus, powerAmount * amount1()) * (1 + powerAmount * amount());
	}
	
	@SpirePatch(clz = VulnerablePower.class, method = "atDamageReceive")
	public static class VulnerablePowerPatch {
		@SpireInsertPatch(rloc = 7)
		public static SpireReturn<Float> Insert(VulnerablePower p, float damage, DamageType type) {
			if (!hasRelic() || p.amount < 2 || p.owner.isPlayer)
				return SpireReturn.Continue();
			return SpireReturn.Return((float) finalRate(hasFrog(p) ? 0.75 : 0.5, p.amount));
		}
	}
	
	@SpirePatch(clz = WeakPower.class, method = "atDamageGive")
	public static class WeakPowerPatch {
		@SpireInsertPatch(rloc = 1)
		public static SpireReturn<Float> Insert(WeakPower p, float damage, DamageType type) {
			if (!hasRelic() || p.amount < 2 || p.owner.isPlayer)
				return SpireReturn.Continue();
			return SpireReturn
					.Return((float) (damage * Math.pow((hasCrane(p) ? 0.6F : 0.75F), p.amount * amount() + amount1())));
		}
	}
}
