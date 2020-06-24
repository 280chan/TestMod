package events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;

public abstract class AbstractTestEvent extends AbstractImageEvent {
	
	protected static EventStrings Strings(String ID) {
		return CardCrawlGame.languagePack.getEventString(ID);
	}

	public AbstractTestEvent(String NAME, String DESC, String IMG) {
		super(NAME, DESC, IMG);
	}
	
	protected void logMetric(String result) {
		logMetric(this.title, result);
	}
	
}