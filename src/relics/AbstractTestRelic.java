package relics;

import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomRelic;
import mymod.TestMod;
import utils.MiscMethods;

public abstract class AbstractTestRelic extends CustomRelic implements MiscMethods {
	private static final HashMap<Class<? extends AbstractTestRelic>, HashMap<String, Boolean>> EQUIP =
			new HashMap<Class<? extends AbstractTestRelic>, HashMap<String, Boolean>>();
	private static final HashMap<String, Texture> IMG = new HashMap<String, Texture>();
	private static final HashMap<Class<? extends AbstractTestRelic>, String> IDS = 
			new HashMap<Class<? extends AbstractTestRelic>, String>();
	public boolean isActive = false;
	public boolean show = true;
	public TestTier testTier = TestTier.NORMAL;
	protected static final TestTier BAD = TestTier.BAD;
	protected static final TestTier NORMAL = TestTier.NORMAL;
	protected static final TestTier GOD = TestTier.GOD;

	public static enum TestTier {
		GOD, NORMAL, BAD
	}
	
	protected void setTestTier(TestTier t) {
		this.testTier = t;
	}
	
	public boolean isBad() {
		return this.testTier == BAD;
	}
	
	public boolean sameAs(AbstractRelic r) {
		return this.relicId.equals(r.relicId);
	}
	
	public void setAsInactive() {
		this.isActive = false;
	}
	
	public void setAsActive() {
		this.isActive = true;
	}
	
	public static void addToMap(AbstractTestRelic obj) {
		if (!EQUIP.containsKey(obj.getClass())) {
			HashMap<String, Boolean> equip = new HashMap<String, Boolean>();
			equip.put("equip", false);
			equip.put("unequip", false);
			EQUIP.put(obj.getClass(), equip);
		}
	}
	
	public static boolean tryEquip(AbstractTestRelic obj) {
		return EQUIP.get(obj.getClass()).get("equip");
	}
	
	public static boolean tryUnequip(AbstractTestRelic obj) {
		return EQUIP.get(obj.getClass()).get("unequip");
	}
	
	private static void modifyState(Class<? extends AbstractTestRelic> c, String key, boolean value) {
		EQUIP.get(c).replace(key, value);
	}
	
	protected static void setTryEquip(Class<? extends AbstractTestRelic> c, boolean value) {
		modifyState(c, "equip", value);
	}
	
	protected static void setTryUnequip(Class<? extends AbstractTestRelic> c, boolean value) {
		modifyState(c, "unequip", value);
	}
	
	protected void setTryEquip(boolean value) {
		modifyState(this.getClass(), "equip", value);
	}
	
	protected void setTryUnequip(boolean value) {
		modifyState(this.getClass(), "unequip", value);
	}
	
	protected static <T extends AbstractTestRelic> String shortID(Class<T> c) {
		if (!IDS.containsKey(c)) {
			String tmp = ReflectionHacks.getPrivateStatic(c, "ID");
			IDS.put(c, tmp == null ? c.getSimpleName() : tmp);
		}
		return IDS.get(c);
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends AbstractTestRelic> Class<T> getRelicClass() {
		try {
			return (Class<T>) Class.forName(Stream.of(new Exception().getStackTrace()).map(i -> i.getClassName())
					.filter(s -> !"relics.AbstractTestRelic".equals(s)).findFirst().get());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public AbstractTestRelic(RelicTier tier, LandingSound sfx) {
		this(shortID(getRelicClass()), tier, sfx);
	}
	
	public AbstractTestRelic(RelicTier tier, LandingSound sfx, TestTier tt) {
		this(tier, sfx);
		this.setTestTier(tt);
	}
	
	public AbstractTestRelic(String id, RelicTier tier, LandingSound sfx) {
		this(TestMod.makeID(id), TestMod.relicIMGPath(id), tier, sfx);
	}
	
	public AbstractTestRelic(String id, String path, RelicTier tier, LandingSound sfx) {
		super(id, getTexture(id, path), tier, sfx);
	}
	
	private static Texture getTexture(String id, String path) {
		if (!IMG.containsKey(id)) {
			IMG.put(id, new Texture(Gdx.files.internal(path)));
		}
		return IMG.get(id);
	}
	
	public void show() {
		if (!show)
			return;
		flash();
	    this.addToBot(new RelicAboveCreatureAction(p(), this));
	}
	
	public void postUpdate() {
	}
	
	public void preUpdate() {
	}
	
	public static void equipAction() {
	}
	
	public static void unequipAction() {
	}
	
	public float preChangeMaxHP(float amount) {
		return amount;
	}
	
	public Function<Float, Float> maxHPChanger() {
		return this::preChangeMaxHP;
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	protected void updateDescription() {
		updateDescription(p().chosenClass);
	}
	
}