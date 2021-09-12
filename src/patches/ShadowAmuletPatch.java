package patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import relics.ShadowAmulet;

public class ShadowAmuletPatch {
	@SpirePatch(clz = AbstractCreature.class, method = "loseBlock", paramtypes = {"int", "boolean"})
	public static class AbstractCreaturePatch {
		@SpireInsertPatch(rloc = 3)
		public static void Insert(AbstractCreature c, int amount, boolean noAnimation) {
			if (c.isPlayer)
				ShadowAmulet.getThis().forEach(r -> {r.loseblock(Math.min(amount, c.currentBlock));});
		}
	}
}
