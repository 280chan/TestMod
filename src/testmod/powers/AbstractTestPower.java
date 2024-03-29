package testmod.powers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.UnaryOperator;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import testmod.mymod.TestMod;
import testmod.utils.MiscMethods;

public abstract class AbstractTestPower extends AbstractPower implements MiscMethods {
	protected static HashMap<Class<? extends AbstractTestPower>, UnaryOperator<AbstractTestPower>> MAP =
			new HashMap<Class<? extends AbstractTestPower>, UnaryOperator<AbstractTestPower>>();
	protected static ArrayList<Class<? extends AbstractTestPower>> SKIP =
			new ArrayList<Class<? extends AbstractTestPower>>();
	protected static ArrayList<Class<? extends AbstractTestPower>> TOP =
			new ArrayList<Class<? extends AbstractTestPower>>();
	
	private String IMGPath(String shortID) {
		return TestMod.powerIMGPath(shortID.endsWith("Up") ? shortID.substring(0, shortID.length() - 2) : shortID);
	}

	public AbstractTestPower(String shortID) {
		this.ID = TestMod.makeID(shortID);
		this.img = ImageMaster.loadImage(this.IMGPath(shortID));
	}
	
	public AbstractTestPower(String shortID, String region) {
		this.ID = TestMod.makeID(shortID);
		this.loadRegion(region);
	}
	
	public AbstractTestPower() {
		this(shortID(getPowerClass()));
		this.name = (this instanceof InvisiblePower) ? "" : name();
	}
	
	public void updateDescription() {
		 this.description = desc(0);
	}
	
	protected void setRegion(String region) {
		this.img = null;
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
	
	protected String shortID() {
		return shortID(this.getClass());
	}
	
	protected static <T extends AbstractTestPower> String shortID(Class<T> c) {
		IDS.putIfAbsent(c, MiscMethods.getIDForPowerWithoutLog(c));
		return IDS.get(c);
	}
	
	private static <T extends AbstractTestPower> Class<T> getPowerClass() {
		return MISC.get(AbstractTestPower.class);
	}

	private static final HashMap<String, PowerStrings> PS = new HashMap<String, PowerStrings>();
	private static final HashMap<Class<? extends AbstractTestPower>, String> IDS = 
			new HashMap<Class<? extends AbstractTestPower>, String>();

	protected static PowerStrings Strings(String ID) {
		PS.putIfAbsent(ID, CardCrawlGame.languagePack.getPowerStrings(TestMod.makeID(ID)));
		return PS.get(ID);
	}
	
	protected static String name(String id) {
		return Strings(id).NAME;
	}
	
	protected static String[] desc(String id) {
		return Strings(id).DESCRIPTIONS;
	}
	
	protected String name() {
		return name(shortID());
	}
	
	protected String desc(int index) {
		return desc(shortID()).length > index ? desc(shortID())[index] : this.ID + "描述下标越界";
	}
	
}
