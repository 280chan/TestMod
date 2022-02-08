package events;

import java.util.stream.Stream;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;

import basemod.BaseMod;
import mymod.TestMod;
import utils.MiscMethods;

public abstract class AbstractTestEvent extends AbstractImageEvent implements MiscMethods {
	
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