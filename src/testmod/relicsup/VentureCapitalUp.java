package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import testmod.relics.VentureCapital;
import testmod.utils.CounterKeeper;

public class VentureCapitalUp extends AbstractUpgradedRelic implements CounterKeeper, ClickableRelic {
	private boolean lock = false;
	private static final int LIMIT = 1999999900;
	
	public String getUpdatedDescription() {
		return this.counter < 1 ? DESCRIPTIONS[0]
				: DESCRIPTIONS[0] + DESCRIPTIONS[1] + goldRatePercent() + DESCRIPTIONS[2];
	}

	private String goldRatePercent() {
		return 100 + this.counter + "";
	}
	
	private double gainGoldRate() {
		return 1 + this.counter / VentureCapital.PERCENTAGE;
	}
	
	public void onEquip() {
		this.counter = 0;
    }
	
	private void increaseCounter(int gold) {
		if (this.counter * 1.0 + gold >= LIMIT)
			this.counter = LIMIT;
		else
			this.counter += gold;
		this.updateDescription();
		this.show();
	}
	
	public double gainGold(double amount) {
		double tmp = amount * gainGoldRate();
		if (tmp > 0)
			this.show();
		return tmp;
	}
	
	public void onLoseGold() {
		if (this.lock)
			return;
		this.increaseCounter(1);
	}

	@Override
	public void onRightClick() {
		if (p().gold > 0) {
			boolean key = p().gold >= 1000;
			if (key)
				this.addRandomKey();
			this.lock = true;
			this.increaseCounter(key ? p().gold - 1000 : p().gold);
			p().loseGold(p().gold);
			this.lock = false;
		}
	}

}