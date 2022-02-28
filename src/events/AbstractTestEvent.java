package events;

import java.util.stream.Stream;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import basemod.BaseMod;
import mymod.TestMod;
import utils.MiscMethods;

public abstract class AbstractTestEvent extends AbstractImageEvent implements MiscMethods {
	protected static final Phase INTRO = Phase.INTRO;
	protected static final Phase CHOOSE = Phase.CHOOSE;
	protected static final Phase COMPLETE = Phase.COMPLETE;
	protected Phase screen = INTRO;

	protected static enum Phase {
		INTRO, CHOOSE, COMPLETE;
		private Phase() {
		}
	}
	
	protected void choose() {
		this.screen = CHOOSE;
	}
	
	protected void complete() {
		this.screen = COMPLETE;
	}
	
	protected abstract void intro();
	
	protected abstract void choose(int choice);
	
	protected void buttonEffect(int buttonPressed) {
		switch (this.screen) {
		case INTRO:
			this.intro();
			this.choose();
			break;
		case CHOOSE:
			this.choose(buttonPressed);
			this.complete();
			break;
		case COMPLETE:
			openMap();
		}
	}
	
	private static EventStrings Strings() {
		return CardCrawlGame.languagePack.getEventString(getID());
	}
	
	protected static String[] desc() {
		return Strings().DESCRIPTIONS;
	}
	
	protected static String[] option() {
		return Strings().OPTIONS;
	}
	
	public AbstractTestEvent() {
		super(Strings().NAME, desc()[0], TestMod.eventIMGPath("BoxForYourself"));
		this.imageEventText.setDialogOption(option()[0]);
	}
	
	protected void logMetric(String result) {
		logMetric(this.title, result);
	}
	
	private static String getID() {
		return TestMod.makeID(MiscMethods.getIDWithoutLog(getEventClass()));
	}
	
	public static <T extends AbstractTestEvent> void addEvent(Class<T> c) {
		BaseMod.addEvent(TestMod.makeID(MiscMethods.getIDWithoutLog(c)), c);
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends AbstractTestEvent> Class<T> getEventClass() {
		try {
			return (Class<T>) Class.forName(Stream.of(new Exception().getStackTrace()).map(i -> i.getClassName())
					.filter(s -> !"events.AbstractTestEvent".equals(s)).findFirst().get());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}