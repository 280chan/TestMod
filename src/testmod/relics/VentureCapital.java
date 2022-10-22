package testmod.relics;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class VentureCapital extends AbstractTestRelic {
	private static final int LOSE_GOLD_AMOUNT = 1;
	public static final double PERCENTAGE = 100.0;
	
	public String getUpdatedDescription() {
		return this.counter < 1 ? DESCRIPTIONS[0]
				: DESCRIPTIONS[0] + DESCRIPTIONS[1] + goldRatePercent() + DESCRIPTIONS[2];
	}

	private String goldRatePercent() {
		return 100 + this.counter + "";
	}
	
	private double gainGoldRate() {
		return 1 + this.counter / PERCENTAGE;
	}
	
	public void onEquip() {
		this.counter = 0;
		if (p().gold > 0)
			this.increaseCounter(p().gold);
		p().loseGold(p().gold);
	}
	
	private void increaseCounter(int gold) {
		this.counter += gold;
		this.updateDescription();
	}
	
	public double gainGold(double amount) {
		double tmp = amount * gainGoldRate();
		if (tmp >= LOSE_GOLD_AMOUNT) {
			tmp -= LOSE_GOLD_AMOUNT;
			this.increaseCounter(LOSE_GOLD_AMOUNT);
			this.flash();
		}
		return tmp;
	}

	public boolean canSpawn() {
		return (Settings.isEndless) || (AbstractDungeon.actNum < 3);
	}

}