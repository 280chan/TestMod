package patches;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import utils.MiscMethods;

public class ChangeGainGoldAmountPatch {
	@SpirePatch(clz = AbstractPlayer.class, method = "gainGold")
	public static class AbstractPlayerPatch {
		@SpireInsertPatch(rloc = 8)
		public static void Insert(AbstractPlayer player, @ByRef int[] amount) {
			double input = amount[0];
			if (hasEnemy()) {
				for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
					input = modifyGoldAmountThroughList(m.powers, input);
			}
			input = modifyGoldAmountThroughList(player.powers, input);
			input = modifyGoldAmountThroughList(player.relics, input);
			amount[0] = (int) (input + 0.01);
		}
	}
	
	private static double modifyGoldAmountThroughList(ArrayList<? extends Object> list, double input) {
		for (Object p : list)
			if (p instanceof MiscMethods)
				input = ((MiscMethods) p).gainGold(input);
		return input;
	}
	
	private static boolean hasEnemy() {
		if (AbstractDungeon.currMapNode == null)
			return false;
		if (AbstractDungeon.getCurrRoom() == null)
			return false;
		if (AbstractDungeon.getCurrRoom().monsters == null)
			return false;
		if (AbstractDungeon.getCurrRoom().monsters.monsters == null)
			return false;
		return true;
	}
}
