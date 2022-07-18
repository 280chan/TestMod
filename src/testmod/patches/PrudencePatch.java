package testmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.BlueCandle;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import testmod.relics.Prudence;
import testmod.relicsup.PrudenceUp;
import testmod.utils.MiscMethods;

public class PrudencePatch {
	public static boolean hasRelic() {
		return MiscMethods.MISC.relicStream(Prudence.class).count() > 0;
	}
	
	public static boolean hasUp() {
		return MiscMethods.MISC.relicStream(PrudenceUp.class).count() > 0;
	}
	
	@SpirePatch(clz = BlueCandle.class, method = "onUseCard")
	public static class BlueCandlePatch {
		public static SpireReturn<Void> Prefix(BlueCandle r, AbstractCard c, UseCardAction action) {
			return hasUp() ? SpireReturn.Return() : SpireReturn.Continue();
		}
	}
	
	public static boolean canPlay(AbstractCard c) {
		return (hasRelic() && hasEnoughEnergy(c)) || (hasUp() && !PrudenceUp.CARDS.contains(c));
	}
	
	private static boolean hasEnoughEnergy(AbstractCard c) {
		if (AbstractDungeon.actionManager.turnHasEnded)
			return false;
		return (EnergyPanel.totalCount >= c.costForTurn) || (c.freeToPlay()) || (c.isInAutoplay);
	}
	
	@SpirePatch(clz = AbstractCard.class, method = "hasEnoughEnergy")
	public static class AbstractCardHasEnoughEnergyPatch {
		public static SpireReturn<Boolean> Prefix(AbstractCard c) {
			return canPlay(c) ? SpireReturn.Return(true) : SpireReturn.Continue();
		}
	}
	
	private static ExprEditor editor(String varName) {
		return new ExprEditor() {
			public void edit(MethodCall mc) throws CannotCompileException {
				if (mc.getClassName().equals(AbstractCard.class.getName())
						&& mc.getMethodName().equals("canUse")) {
					mc.replace("$_ = $proceed($$) || testmod.patches.PrudencePatch.canPlay(" + varName + ");");
				}
			}
		};
	}
	
	@SpirePatch(clz = AbstractPlayer.class, method = "updateSingleTargetInput")
	public static class AbstractPlayerUpdateSingleTargetInputPatch {
		public static ExprEditor Instrument() {
			return editor("hoveredCard");
		}
	}
	
	@SpirePatch(clz = AbstractPlayer.class, method = "clickAndDragCards")
	public static class AbstractPlayerClickAndDragCardsPatch {
		public static ExprEditor Instrument() {
			return editor("hoveredCard");
		}
	}
	
	@SpirePatch(clz = AbstractPlayer.class, method = "releaseCard")
	public static class AbstractPlayerReleaseCardPatch {
		public static ExprEditor Instrument() {
			return editor("hoveredCard");
		}
	}
	
	@SpirePatch(clz = CardGroup.class, method = "glowCheck")
	public static class CardGroupGlowCheckPatch {
		public static ExprEditor Instrument() {
			return editor("c");
		}
	}
	
	@SpirePatch(clz = GameActionManager.class, method = "getNextAction")
	public static class GameActionManagerGetNextActionPatch {
		public static ExprEditor Instrument() {
			return editor("c");
		}
	}
}
