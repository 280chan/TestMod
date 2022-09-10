package testmod.events;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.ObtainKeyEffect;
import testmod.relicsup.AllUpgradeRelic;
import testmod.relicsup.HarvestTotemUp;

public class GoblinJeweler extends AbstractTestEvent {
	private static final int[] BASE = { 100, 200, 150 };
	private static final String[] K = { "R", "G", "B" };
	private int totalGoldLoss = 0;
	private String chosen = "";
	
	private static int price(int type, int adding) {
		return BASE[type] * (1 + adding + AllUpgradeRelic.MultiKey.KEY[type]);
	}
	
	private static int price(int type) {
		return price(type, 0);
	}
	
	public static int minGold() {
		return MISC.getNaturalNumberList(3).stream().mapToInt(i -> price(i)).min().orElse(100);
	}
	
	private void updateOption(int current) {
		for (int i = 0; i < 3; i++) {
			int price = i == current ? price(i, (int) (MISC.relicStream(HarvestTotemUp.class).count() + 1)) : price(i);
			boolean f = p().gold < price;
			this.imageEventText.updateDialogOption(i, option()[f ? 6 : 5] + option()[i + 1] + price + option()[4], f);
		}
	}

	@Override
	protected void intro() {
		this.imageEventText.updateBodyText(desc()[1]);
		this.imageEventText.removeDialogOption(0);
		this.updateOption(-1);
		this.imageEventText.setDialogOption(option()[7]);
	}

	@Override
	protected void choose(int i) {
		if (i == 3) {
			this.imageEventText.updateBodyText(desc()[this.totalGoldLoss > 0 ? 2 : 3]);
			this.imageEventText.updateDialogOption(0, option()[7]);
			this.imageEventText.clearRemainingOptions();
			if (this.totalGoldLoss > 0) {
				logMetricLoseGold(this.title, this.chosen, this.totalGoldLoss);
			} else {
				logMetric("Ignored");
			}
		} else {
			this.totalGoldLoss += price(i);
			this.chosen += K[i];
			p().loseGold(price(i));
			AbstractDungeon.topLevelEffectsQueue.add(new ObtainKeyEffect(ObtainKeyEffect.KeyColor.values()[i]));
			this.updateOption(i);
			this.choose();
		}
	}
}