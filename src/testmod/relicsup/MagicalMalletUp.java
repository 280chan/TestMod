package testmod.relicsup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import testmod.mymod.TestMod;

public class MagicalMalletUp extends AbstractUpgradedRelic implements ClickableRelic {
	private boolean playerTurn = false, used = false;
	
	public void atPreBattle() {
		used = false;
    }
	
	public void atTurnStart() {
		playerTurn = true;
		this.togglePulse(this, !used);
    }
	
	public void onPlayerEndTurn() {
		this.togglePulse(this, playerTurn = false);
    }
	
	private IntStream costs(ArrayList<AbstractCard> hand) {
		return hand.stream().filter(c -> c.cost >= 0).mapToInt(c -> c.costForTurn);
	}
	
	public void atTurnStartPostDraw() {
		this.addTmpActionToBot(() -> {
			ArrayList<AbstractCard> hand = p().hand.group;
			if (hand.isEmpty())
				return;
			int min = costs(hand).min().orElse(999);
			int max = costs(hand).max().orElse(-999);
			TestMod.info("最大: " + max + ",最小: " + min);
			if (min == max)
				return;
			ArrayList<AbstractCard> s = hand.stream().filter(c -> c.costForTurn == min).collect(toArrayList());
			hand.stream().filter(c -> c.costForTurn == max).forEach(c -> c.modifyCostForCombat(min - max));
			Collections.shuffle(s, new Random(AbstractDungeon.cardRandomRng.randomLong()));
			s.stream().limit(1).forEach(c -> c.setCostForTurn(c.costForTurn + 1));
			this.show();
		});
    }

	@Override
	public void onRightClick() {
		if (used || !playerTurn)
			return;
		used = true;
		this.addTmpActionToTop(() -> p().hand.group.stream().filter(c -> c.costForTurn > 0)
				.forEach(c -> c.setCostForTurn(c.costForTurn - 1)));
		this.togglePulse(this, false);
	}
	
}