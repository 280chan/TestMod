package testmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.blue.ForceField;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import testmod.relics.Nyarlathotep;
import testmod.relicsup.NyarlathotepUp;
import testmod.utils.MiscMethods;

public class ForceFieldNyarlathotepPatch {
	@SpirePatch(clz = ForceField.class, method = "configureCostsOnNewCard")
	public static class ForceFieldPatch {
		@SpirePrefixPatch
		public static void Prefix(ForceField c) {
			if (MiscMethods.MISC.p() == null || MiscMethods.MISC.p().relics == null)
				return;
			int r = (int) MiscMethods.MISC.relicStream(Nyarlathotep.class).count();
			int s = 2 * (int) MiscMethods.MISC.relicStream(NyarlathotepUp.class).count();
			int n = AbstractDungeon.actionManager.cardsPlayedThisCombat.size();
			if (r > 0)
				c.updateCost(- (r + s) * n);
		}
	}
}
