package patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.AbstractPotion.PotionRarity;
import com.megacrit.cardcrawl.potions.FirePotion;

import potions.SpacePotion;
import potions.TestPotion;
import potions.TimePotion;

public class StupidUnknownModCausingNullPotionFixPatch {
	private static PotionRarity current = null;
	
	@SpirePatch(clz = PotionHelper.class, method = "getPotion")
	public static class PotionHelperPatch {
		@SpirePostfixPatch
		public static AbstractPotion Postfix(AbstractPotion _return, String name) {
			if (_return == null && !"Potion Slot".equals(name)) {
				if (current == PotionRarity.COMMON)
					return new FirePotion();
				if (current == PotionRarity.UNCOMMON)
					return new SpacePotion();
				if (current == PotionRarity.RARE)
					return new TimePotion();
				return new TestPotion();
			}
			return _return;
		}
	}
	
	@SpirePatch(clz = AbstractDungeon.class, method = "returnRandomPotion", paramtypez = { PotionRarity.class,
			boolean.class })
	public static class AbstractDungeonPatch {
		@SpirePrefixPatch
		public static void Prefix(PotionRarity rarity, boolean limited) {
			if (rarity != null) {
				current = rarity;
			}
		}

		@SpirePostfixPatch
		public static void Postfix(PotionRarity rarity, boolean limited) {
			current = null;
		}
	}
	
	
}
