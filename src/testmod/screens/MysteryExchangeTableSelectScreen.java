package testmod.screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import testmod.events.MysteryExchangeTable;
import testmod.mymod.TestMod;
import testmod.utils.MiscMethods;

public class MysteryExchangeTableSelectScreen extends RelicSelectScreen implements MiscMethods {
	private static final UIStrings UI = INSTANCE.uiString();
	private boolean gain;
	private static final HashMap<AbstractRelic, AbstractRelic> MAP = new HashMap<AbstractRelic, AbstractRelic>();
	private MysteryExchangeTable e;
	
	public MysteryExchangeTableSelectScreen(ArrayList<AbstractRelic> c, boolean gain, MysteryExchangeTable e) {
		super(copyList(c), false, UI.TEXT[0], UI.TEXT[1], UI.TEXT[2]);
		this.gain = gain;
		this.e = e;
	}
	
	public MysteryExchangeTableSelectScreen(MysteryExchangeTable e) {
		this(randomList(), true, e);
	}

	@Override
	protected void addRelics() {
	}
	
	private static ArrayList<AbstractRelic> copyList(ArrayList<AbstractRelic> list) {
		return list.stream().map(INSTANCE.split(r -> r.makeCopy(), r -> r))
				.peek(p -> MAP.put(p.getKey(), p.getValue())).map(p -> p.getKey()).collect(INSTANCE.toArrayList());
	}
	
	private static ArrayList<AbstractRelic> randomList() {
		ArrayList<AbstractRelic> tmp = RelicLibrary.specialList.stream().collect(INSTANCE.toArrayList());
		Collections.shuffle(tmp, new Random(AbstractDungeon.miscRng.randomLong()));
		ArrayList<AbstractRelic> ret = tmp.stream().limit(Math.max(tmp.size() / 3, 1)).collect(INSTANCE.toArrayList());
		tmp.clear();
		return ret;
	}
	
	@Override
	protected void afterSelected() {
		if (!gain) {
			p().relics.remove(MAP.get(this.selectedRelic));
			p().reorganizeRelics();
			e.setLose(MAP.get(this.selectedRelic));
			this.rejectSelection = true;
			new MysteryExchangeTableSelectScreen(e).open();
		} else {
			TestMod.obtain(p(), this.selectedRelic, true);
			e.setGainAndLog(this.selectedRelic);
		}
		MAP.clear();
	}

	@Override
	protected void afterCanceled() {
	}

	@Override
	protected String categoryOf(AbstractRelic r) {
		return null;
	}

	@Override
	protected String descriptionOfCategory(String category) {
		return null;
	}
}