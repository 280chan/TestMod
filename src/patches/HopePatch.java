package patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import mymod.TestMod;
import relics.Hope;

public class HopePatch {
	@SpirePatch(clz = GameActionManager.class, method = "getNextAction")
	public static class GameActionManagerPatch {
		@SpireInsertPatch(rloc = 25)
		public static void Insert(GameActionManager gam) {
			if (gam.cardQueue.size() == 1 && ((CardQueueItem) gam.cardQueue.get(0)).isEndTurnAutoPlay) {
				Hope hope = (Hope) AbstractDungeon.player.getRelic(TestMod.makeID(Hope.ID));
				if (hope != null)
					hope.disableUntilTurnEnds();
			}
		}
	}
}
