package testmod.patches;

import java.util.ArrayList;
import java.util.function.UnaryOperator;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatches;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import testmod.utils.MiscMethods;

public class ChangeGainGoldAmountPatch implements MiscMethods {
	private static final int MAX = 2000000000;
	private static final UnaryOperator<Double> T = MISC.t();

	@SpirePatches(value = { @SpirePatch(clz = AbstractPlayer.class, method = "gainGold"),
			@SpirePatch(cls = "bossed.BossedEctoplasm$GainGold", method = "Prefix", optional = true) })
	public static class AbstractPlayerPatchAndStupidBossedModPatch {
		@SpireInsertPatch(locator = Locator.class)
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
		
		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardCrawlGame.class, "goldGained");
				return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
			}
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
