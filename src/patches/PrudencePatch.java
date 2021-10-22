package patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import mymod.TestMod;
import relics.Hope;

public class PrudencePatch {
	public static boolean hasRelic(AbstractPlayer p) {
		return p.hasRelic(TestMod.makeID("Prudence"));
	}

	@SpirePatch(clz = AbstractCard.class, method = "canUse")
	public static class AbstractCardCanUsePatch {
		public static SpireReturn<Boolean> Prefix(AbstractCard c, AbstractPlayer p, AbstractMonster m) {
			if (hasRelic(p) && hasEnoughEnergy(c)) {
				if (c.type == CardType.CURSE || c.type == CardType.STATUS)
					c.exhaust = true;
				return SpireReturn.Return(true);
			}
			return SpireReturn.Continue();
		}
	}
	
	private static boolean hasEnoughEnergy(AbstractCard c) {
		if (AbstractDungeon.actionManager.turnHasEnded)
			return false;
		if ((EnergyPanel.totalCount >= c.costForTurn) || (c.freeToPlay()) || (c.isInAutoplay))
			return true;
		return false;
	}
	
	@SpirePatch(clz = AbstractCard.class, method = "hasEnoughEnergy")
	public static class AbstractCardHasEnoughEnergyPatch {
		public static SpireReturn<Boolean> Prefix(AbstractCard c) {
			if (hasRelic(AbstractDungeon.player) && hasEnoughEnergy(c))
				return SpireReturn.Return(true);
			return SpireReturn.Continue();
		}
	}
	
	//@SpirePatch(clz = AbstractPlayer.class, method = "updateSingleTargetInput")
	public static class AbstractPlayerUpdateSingleTargetInputPatch {
		//@SpireInsertPatch(rloc = 70)
		public static void Insert(GameActionManager gam) {
			if (gam.cardQueue.size() == 1 && ((CardQueueItem) gam.cardQueue.get(0)).isEndTurnAutoPlay) {
				Hope hope = (Hope) AbstractDungeon.player.getRelic(TestMod.makeID("Hope"));
				if (hope != null)
					hope.disableUntilTurnEnds();
			}
		}
	}
	
	
}
