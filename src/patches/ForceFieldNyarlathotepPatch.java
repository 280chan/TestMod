package patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.blue.ForceField;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import relics.Nyarlathotep;

public class ForceFieldNyarlathotepPatch {
	@SpirePatch(clz = ForceField.class, method = "configureCostsOnNewCard")
	public static class ForceFieldPatch {
		@SpirePrefixPatch
		public static void Prefix(ForceField c) {
			if (Nyarlathotep.hasThis()) {
				c.updateCost(-AbstractDungeon.actionManager.cardsPlayedThisCombat.size());
			}
		}
	}
}
