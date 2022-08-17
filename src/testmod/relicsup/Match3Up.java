package testmod.relicsup;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;

public class Match3Up extends AbstractUpgradedRelic {
	
	public void onEquip() {
		this.reduceEnergy();
	}
	
	public void onUnequip() {
		this.addEnergy();
	}
	
	private IntStream get() {
		return IntStream.concat(IntStream.of(p().currentHealth, p().gold), Stream
				.of(p().relics, p().drawPile.group, p().hand.group, p().discardPile.group).mapToInt(a -> a.size()));
	}
	
	public void atTurnStartPostDraw() {
		this.show();
		int s = (int) get().filter(a -> a % 3 == 0).count();
		if (s > 0)
			this.atb(new GainEnergyAction(s));
		if (s < 6)
			this.atb(new DrawCardAction(6 - s));
    }

}