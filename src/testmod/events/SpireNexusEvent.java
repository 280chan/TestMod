package testmod.events;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.localization.EventStrings;

import basemod.BaseMod;
import basemod.eventUtil.EventUtils;
import testmod.relics.SpireNexus;
import testmod.relicsup.SpireNexusUp;

public class SpireNexusEvent extends AbstractTestEvent {
	private static final HashMap<String, Supplier<Boolean>> EVENTS = new HashMap<String, Supplier<Boolean>>();
	private ArrayList<String> tmp;
	
	public SpireNexusEvent() {
		super();
		if (!SpireNexus.skipEffect) {
			this.relicStream(SpireNexus.class).forEach(r -> r.flash());
			this.relicStream(SpireNexusUp.class).forEach(r -> r.flash());
		}
	}
	
	@Override
	protected void intro() {
		int size = this.relicStream(SpireNexusUp.class).count() == 0 ? 3 : 5;
		this.imageEventText.updateBodyText(desc()[0]);
		this.imageEventText.removeDialogOption(0);
		tmp = Stream.of(AbstractDungeon.shrineList, AbstractDungeon.specialOneTimeEventList, AbstractDungeon.eventList)
				.flatMap(l -> l.stream()).filter(SpireNexusEvent::canEventSpawn).collect(toArrayList());
		Collections.shuffle(tmp, new Random(AbstractDungeon.eventRng.copy().randomLong()));
		ArrayList<String> ids = tmp.stream().limit(size).collect(toArrayList());
		tmp.clear();
		tmp = ids;
		tmp.forEach(s -> this.imageEventText.setDialogOption(this.getNameFor(s)));
		if (tmp.size() < size)
			imageEventText.setDialogOption(option()[1], true);
		if (tmp.isEmpty())
			imageEventText.setDialogOption(option()[2]);
	}
	
	private String getNameFor(String id) {
		String n = EventHelper.getEventName(id);
		if (n != null && !n.equals("")) {
			return n;
		}
		EventStrings es = CardCrawlGame.languagePack.getEventString(id);
		if (!"[MISSING_NAME]".equals(es.NAME)) {
			return es.NAME;
		}
		es = Stream.of(BaseMod.getEvent(id).getFields()).filter(f -> f.getType() == EventStrings.class).map(f -> {
			try {
				f.setAccessible(true);
				return Modifier.isStatic(f.getModifiers()) ? (EventStrings) f.get(null) : null;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
		}).filter(f -> f != null).findFirst().orElse(null);
		return es == null || "[MISSING_NAME]".equals(es.NAME) ? id : es.NAME;
	}

	@Override
	protected void choose(int choice) {
		if (tmp.isEmpty()) {
			logMetric("Ignored");
			openMap();
			return;
		}
		this.choose();
		AbstractEvent next = EventHelper.getEvent(tmp.get(choice));
		AbstractDungeon.getCurrRoom().event = next;
		next.onEnterRoom();
		if (!(next instanceof AbstractImageEvent)) {
			GenericEventDialog.hide();
			AbstractDungeon.rs = AbstractDungeon.RenderScene.NORMAL;
		}
		Stream.of(AbstractDungeon.shrineList, AbstractDungeon.specialOneTimeEventList, AbstractDungeon.eventList)
				.forEach(l -> l.remove(tmp.get(choice)));
		tmp.clear();
	}
	
	private static boolean dungeon(String... valid) {
		return Stream.of(valid).anyMatch(AbstractDungeon.id::equals);
	}
	
	private static boolean gold(int gold) {
		return AbstractDungeon.player.gold >= gold;
	}
	
	private static boolean floor(int floor) {
		return AbstractDungeon.floorNum > floor;
	}
	
	static {
		EVENTS.put("Fountain of Cleansing", () -> AbstractDungeon.player.isCursed());
		EVENTS.put("Designer", () -> dungeon("TheCity", "TheBeyond") && gold(75));
		EVENTS.put("Duplicator", () -> dungeon("TheCity", "TheBeyond"));
		EVENTS.put("FaceTrader", () -> dungeon("TheCity", "Exordium"));
		EVENTS.put("Knowing Skull", () -> dungeon("TheCity") && AbstractDungeon.player.currentHealth > 12);
		EVENTS.put("N'loth", () -> dungeon("TheCity") && AbstractDungeon.player.relics.size() >= 2);
		EVENTS.put("The Joust", () -> dungeon("TheCity") && gold(50));
		EVENTS.put("The Woman in Blue", () -> gold(50));
		EVENTS.put("SecretPortal", () -> dungeon("TheBeyond") && CardCrawlGame.playtime >= 800.0F);
		EVENTS.put("Dead Adventurer", () -> floor(6));
		EVENTS.put("Mushrooms", () -> floor(6));
		EVENTS.put("The Moai Head", () -> AbstractDungeon.player.hasRelic("Golden Idol")
				|| AbstractDungeon.player.currentHealth / AbstractDungeon.player.maxHealth <= 0.5F);
		EVENTS.put("The Cleric", () -> gold(35));
		EVENTS.put("Beggar", () -> gold(75));
		EVENTS.put("Colosseum", () -> AbstractDungeon.currMapNode != null && gold(75));
	}
	
	private static boolean canConditionModEventSpawn(String id) {
		boolean r = true;
		if (EventUtils.normalEvents.containsKey(id)) {
			r &= EventUtils.normalEvents.get(id).isValid();
			if (EventUtils.normalEventBonusConditions.containsKey(id)) {
				r &= EventUtils.normalEventBonusConditions.get(id).test();
			}
		}
		if (EventUtils.shrineEvents.containsKey(id)) {
			r &= EventUtils.shrineEvents.get(id).isValid();
			if (EventUtils.specialEventBonusConditions.containsKey(id)) {
				r &= EventUtils.specialEventBonusConditions.get(id).test();
			}
		}
		if (EventUtils.oneTimeEvents.containsKey(id)) {
			r &= EventUtils.oneTimeEvents.get(id).isValid();
		}
		return r;
	}
	
	private static boolean canEventSpawn(String id) {
		return canConditionModEventSpawn(id) && (!EVENTS.containsKey(id) || EVENTS.get(id).get());
	}
	
}