package events;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;

import mymod.TestMod;
import screens.MysteryExchangeTableSelectScreen;

public class MysteryExchangeTable extends AbstractTestEvent {

	private AbstractRelic lose, gain;
	
	@Override
	protected void intro() {
		boolean noRelic = p().relics.stream().noneMatch(r -> r.tier == RelicTier.SPECIAL);
		this.imageEventText.updateBodyText(desc()[noRelic ? 4 : 1]);
		this.imageEventText.updateDialogOption(0, option()[1], noRelic);
		this.imageEventText.setDialogOption(option()[2]);
	}

	@Override
	protected void choose(int choice) {
		switch (choice) {
		case 0:
			new MysteryExchangeTableSelectScreen(p().relics.stream().filter(r -> r.tier == RelicTier.SPECIAL)
					.collect(toArrayList()), false, this).open();
			this.imageEventText.updateBodyText(desc()[2]);
			break;
		default:
			logMetric("Ignored");
		}
		this.imageEventText.updateDialogOption(0, option()[3]);
		this.imageEventText.clearRemainingOptions();
	}
	
	public void setLose(AbstractRelic r) {
		this.lose = r;
	}
	
	public void setGainAndLog(AbstractRelic r) {
		this.gain = r;
		this.imageEventText.updateBodyText(desc()[3]);
		logMetricRelicSwap(title, "Swap Relic", gain, lose);
		TestMod.seen = true;
	}
	
}