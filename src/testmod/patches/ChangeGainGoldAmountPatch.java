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
	@SpirePatch(clz = AbstractPlayer.class, method = "gainGold")
	public static class AbstractPlayerPatch {
		@SpireInsertPatch(rloc = 8)
		public static void Insert(AbstractPlayer player, @ByRef int[] amount) {
			double input = amount[0];
			ArrayList<UnaryOperator<Double>> list = new ArrayList<UnaryOperator<Double>>();
			if (hasEnemy()) {
				list.add(INSTANCE.chain(AbstractDungeon.getMonsters().monsters.stream().map(c -> operator(c))));
			}
			list.add(operator(player));
			list.add(operator(player.relics));
			input = INSTANCE.chain(list.stream()).apply(input);
			if (input < 0)
				input = 0;
			amount[0] = (int) (input + 0.01);
		}
	}
	
	private static UnaryOperator<Double> operator(AbstractCreature c) {
		return INSTANCE.chain(c.powers.stream().filter(o -> o instanceof MiscMethods)
				.map(o -> INSTANCE.get(((MiscMethods) o)::gainGold)));
	}
	
	private static UnaryOperator<Double> operator(ArrayList<? extends Object> list) {
		return INSTANCE.chain(list.stream().filter(o -> o instanceof MiscMethods)
				.map(o -> INSTANCE.get(((MiscMethods) o)::gainGold)));
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
