package events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.localization.EventStrings;

public class SpireNexusEvent extends AbstractTestEvent {
	private ArrayList<String> tmp;
	
	@Override
	protected void intro() {
		this.imageEventText.updateBodyText(desc()[0]);
		this.imageEventText.removeDialogOption(0);
		tmp = Stream.of(AbstractDungeon.shrineList, AbstractDungeon.specialOneTimeEventList, AbstractDungeon.eventList)
				.flatMap(l -> l.stream()).collect(toArrayList());
		Collections.shuffle(tmp,
				new Random(new com.megacrit.cardcrawl.random.Random(Settings.seed, AbstractDungeon.eventRng.counter)
						.randomLong()));
		tmp = tmp.stream().limit(3).collect(toArrayList());
		tmp.forEach(s -> this.imageEventText.setDialogOption(this.getNameFor(s)));
		if (tmp.size() < 3)
			imageEventText.setDialogOption(option()[1], true);
		if (tmp.isEmpty())
			imageEventText.setDialogOption(option()[2]);
	}
	
	private String getNameFor(String id) {
		String n = EventHelper.getEventName(id);
		if (n == null || n.equals("")) {
			EventStrings es = CardCrawlGame.languagePack.getEventString(id);
			return "[MISSING_NAME]".equals(es.NAME) ? id : es.NAME;
		} else {
			return n;
		}
	}

	@Override
	protected void choose(int choice) {
		if (tmp.isEmpty()) {
			logMetric("Ignored");
			openMap();
			return;
		}
		this.choose();
		AbstractDungeon.getCurrRoom().event = EventHelper.getEvent(tmp.get(choice));
		AbstractDungeon.getCurrRoom().event.onEnterRoom();
		Stream.of(AbstractDungeon.shrineList, AbstractDungeon.specialOneTimeEventList, AbstractDungeon.eventList)
				.forEach(l -> l.remove(tmp.get(choice)));
		tmp.clear();
	}
	
}