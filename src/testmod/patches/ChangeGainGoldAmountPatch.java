package testmod.patches;

import java.util.ArrayList;
import java.util.function.UnaryOperator;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

import testmod.utils.MiscMethods;

public class ChangeGainGoldAmountPatch implements MiscMethods {
	private static final int MAX = 2000000000;
	private static final UnaryOperator<Double> T = MISC.t();
	
	@SpirePatch(clz = AbstractPlayer.class, method = "gainGold")
	public static class AbstractPlayerPatch {
		@SpireInsertPatch(rloc = 8)
		public static void Insert(AbstractPlayer player, @ByRef int[] amount) {
			double input = amount[0];
			ArrayList<UnaryOperator<Double>> list = new ArrayList<UnaryOperator<Double>>();
			if (MISC.hasEnemies()) {
				list.add(AbstractDungeon.getMonsters().monsters.stream().map(c -> operator(c)).reduce(T, MISC::chain));
			}
			list.add(operator(player));
			list.add(operator(player.relics));
			input = list.stream().reduce(T, MISC::chain).apply(input);
			if (input < 0)
				input = 0;
			amount[0] = input + player.gold > MAX ? (player.gold <= 0 ? MAX : MAX - player.gold) : (int) (input + 0.01);
			list.clear();
		}
	}
	
	private static UnaryOperator<Double> operator(AbstractCreature c) {
		return operator(c.powers);
	}
	
	private static UnaryOperator<Double> operator(ArrayList<?> list) {
		return list.stream().filter(o -> o instanceof MiscMethods).map(o -> MISC.get(((MiscMethods) o)::gainGold))
				.reduce(T, MISC::chain);
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
