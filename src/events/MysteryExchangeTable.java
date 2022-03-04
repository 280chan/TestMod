package events;

import java.util.ArrayList;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;

import mymod.TestMod;
import screens.MysteryExchangeTableSelectScreen;

public class MysteryExchangeTable extends AbstractTestEvent {

	private AbstractRelic lose, gain;
	
	@Override
	protected void intro() {
		boolean noRelic = p().relics.stream().noneMatch(r -> r.tier == RelicTier.SPECIAL);
		ArrayList<AbstractRelic> l = p().relics.stream().filter(r -> r.tier != RelicTier.SPECIAL).collect(toArrayList());
		lose = l.isEmpty() ? null : TestMod.randomItem(l, AbstractDungeon.miscRng);
		this.imageEventText.updateBodyText(desc()[noRelic && lose == null ? 4 : 1]);
		this.imageEventText.updateDialogOption(0, option()[noRelic ? 7 : 1], noRelic);
		if (lose == null)
			this.imageEventText.setDialogOption(option()[4], true);
		else
			this.imageEventText.setDialogOption(option()[5] + lose.name + option()[6], lose);
		this.imageEventText.setDialogOption(option()[2]);
		l.clear();
	}

	@Override
	protected void choose(int choice) {
		switch (choice) {
		case 0:
			new MysteryExchangeTableSelectScreen(p().relics.stream().filter(r -> r.tier == RelicTier.SPECIAL)
					.collect(toArrayList()), false, this).open();
			this.imageEventText.updateBodyText(desc()[2]);
			break;
		case 1:
			p().relics.remove(lose);
			p().reorganizeRelics();
			new MysteryExchangeTableSelectScreen(RelicLibrary.specialList, true, this).open();
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
	}
	
}