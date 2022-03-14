package testmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class StupidAbstractMonsterIntangiblePatch {
	
	@SpirePatch(clz = AbstractMonster.class, method = "damage")
	public static class AbstractMonsterPatch {
		@SpirePrefixPatch
		public static void Prefix(AbstractMonster m, DamageInfo info) {
			if (info.output > 0 && m.hasPower("Intangible")) {
				info.output = 1;
			}
		}
	}
	
}
