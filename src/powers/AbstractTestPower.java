package powers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.UnaryOperator;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;
import utils.MiscMethods;

public abstract class AbstractTestPower extends AbstractPower implements MiscMethods {
	protected static HashMap<Class<? extends AbstractTestPower>, UnaryOperator<AbstractTestPower>> MAP =
			new HashMap<Class<? extends AbstractTestPower>, UnaryOperator<AbstractTestPower>>();
	protected static ArrayList<Class<? extends AbstractTestPower>> SKIP =
			new ArrayList<Class<? extends AbstractTestPower>>();
	protected static ArrayList<Class<? extends AbstractTestPower>> TOP =
			new ArrayList<Class<? extends AbstractTestPower>>();

	protected static PowerStrings Strings(String shortID) {
		return CardCrawlGame.languagePack.getPowerStrings(TestMod.makeID(shortID));
	}
	
	private String IMGPath(String shortID) {
		return TestMod.powerIMGPath(shortID);
	}

	public AbstractTestPower(String shortID) {
		this.ID = TestMod.makeID(shortID);
		this.img = ImageMaster.loadImage(this.IMGPath(shortID));
	}
	
	public AbstractTestPower(String shortID, String region) {
		this.ID = TestMod.makeID(shortID);
		this.loadRegion(region);
	}
	
	public void stackPower(int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}
	
	public void onRemove() {
		Class<? extends AbstractTestPower> c = this.getClass();
		if (MAP.containsKey(c)) {
			regainPowerOnRemove(this, MAP.get(c), SKIP.contains(c), TOP.contains(c));
		}
	}
	
	protected static void addMap(Class<? extends AbstractTestPower> c, UnaryOperator<AbstractTestPower> f) {
		MAP.putIfAbsent(c, f);
	}
	
	protected void addMap(UnaryOperator<AbstractTestPower> f) {
		MAP.putIfAbsent(this.getClass(), f);
	}
	
	protected void addMap(UnaryOperator<AbstractTestPower> f, boolean noStack, boolean top) {
		Class<? extends AbstractTestPower> c = this.getClass();
		if (!TOP.contains(c) && top)
			TOP.add(c);
		if (!SKIP.contains(c) && noStack)
			SKIP.add(c);
		MAP.putIfAbsent(c, f);
	}
	
	protected void addMapToTop(UnaryOperator<AbstractTestPower> f) {
		addMap(f, false, true);
	}
	
	protected void addMapWithSkip(UnaryOperator<AbstractTestPower> f) {
		addMap(f, true, false);
	}
}
