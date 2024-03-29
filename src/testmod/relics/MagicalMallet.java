package testmod.relics;

import java.util.List;
import java.util.stream.IntStream;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.cards.AbstractCard;

import testmod.mymod.TestMod;

public class MagicalMallet extends AbstractTestRelic implements ClickableRelic {
	private boolean playerTurn = false, used = false;
	
	public void onEquip() {
		this.addEnergy();
	}
	
	public void onUnequip() {
		this.reduceEnergy();
	}
	
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
	
	private IntStream costs(List<AbstractCard> hand) {
		return hand.stream().filter(c -> c.cost >= 0).mapToInt(c -> c.costForTurn);
	}
	
	public void atTurnStartPostDraw() {
		this.addTmpActionToBot(() -> {
			List<AbstractCard> hand = p().hand.group;
			if (hand.isEmpty())
				return;
			int min = costs(hand).min().orElse(999);
			int max = costs(hand).max().orElse(-999);
			TestMod.info("最大: " + max + ",最小: " + min);
			if (min == max)
				return;
			hand.stream().filter(c -> c.costForTurn == min || c.costForTurn == max)
					.forEach(c -> c.setCostForTurn(min - c.costForTurn + max));
			this.show();
		});
	}

	@Override
	public void onRightClick() {
		if (!used && playerTurn) {
			used = true;
			this.atTurnStartPostDraw();
			this.togglePulse(this, false);
		}
	}
	
}