package patches;

import java.util.ArrayList;
import java.util.function.UnaryOperator;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

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
			if (input < 0)
				input = 0;
			amount[0] = (int) (input + 0.01);
		}
	}
	
	private static double modifyGoldAmountThroughList(ArrayList<? extends Object> list, double input) {
		return list.stream().filter(o -> o instanceof MiscMethods)
				.map(o -> (UnaryOperator<Double>) (((MiscMethods) o)::gainGold))
				.reduce(a -> a, (a, b) -> c -> b.apply(a.apply(c))).apply(input);
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
	
	@SpirePatch(clz = TopPanel.class, method = "updateGold")
	public static class TopPanelPatch {
		@SpirePostfixPatch
		public static void Postfix(TopPanel panel) {
			AbstractPlayer p = AbstractDungeon.player;
			int delta = (p.displayGold - p.gold);
			if (delta < -999 || delta > 999) {
				p.displayGold -= delta / 10;
			}
		}
	}
}
