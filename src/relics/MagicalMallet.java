package relics;

import java.util.List;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mymod.TestMod;
import utils.MiscMethods;

public class MagicalMallet extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "MagicalMallet";
	
	public MagicalMallet() {
		super(ID, RelicTier.SHOP, LandingSound.MAGICAL);
		this.setTestTier(BAD);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atTurnStartPostDraw() {
		this.addTmpActionToBot(() -> {
			List<AbstractCard> hand = AbstractDungeon.player.hand.group;
			if (hand.isEmpty())
				return;
			int min = hand.stream().filter(c -> c.cost >= 0).map(c -> c.costForTurn).reduce(Integer.MAX_VALUE,
					Integer::min);
			int max = hand.stream().filter(c -> c.cost >= 0).map(c -> c.costForTurn).reduce(Integer.MIN_VALUE,
					Integer::max);
			TestMod.info("最大: " + max + ",最小: " + min);
			if (min == max)
				return;
			hand.stream().filter(c -> c.costForTurn == min || c.costForTurn == max).forEach(c -> {
				c.modifyCostForCombat(max + min - c.costForTurn);
				c.costForTurn = c.cost;
			});
			this.show();
		});
    }
	
}