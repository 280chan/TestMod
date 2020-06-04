package relics;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.abstracts.CustomRelic;
import mymod.TestMod;

public abstract class MyRelic extends CustomRelic{
	public boolean isActive = false;
	public boolean show = true;
	
	private static HashMap<Class<? extends MyRelic>, HashMap<String, Boolean>> EQUIP = new HashMap<Class<? extends MyRelic>, HashMap<String, Boolean>>();
	
	public static void addToMap(MyRelic obj) {
		if (!EQUIP.containsKey(obj.getClass())) {
			HashMap<String, Boolean> equip = new HashMap<String, Boolean>();
			equip.put("equip", false);
			equip.put("unequip", false);
			EQUIP.put(obj.getClass(), equip);
		}
	}
	
	public static boolean tryEquip(MyRelic obj) {
		return EQUIP.get(obj.getClass()).get("equip");
	}
	
	public static boolean tryUnequip(MyRelic obj) {
		return EQUIP.get(obj.getClass()).get("unequip");
	}
	
	private static void modifyState(Class<? extends MyRelic> c, String key, boolean value) {
		EQUIP.get(c).replace(key, value);
	}
	
	protected static void setTryEquip(Class<? extends MyRelic> c, boolean value) {
		modifyState(c, "equip", value);
	}
	
	protected static void setTryUnequip(Class<? extends MyRelic> c, boolean value) {
		modifyState(c, "unequip", value);
	}
	
	protected void setTryEquip(boolean value) {
		modifyState(this.getClass(), "equip", value);
	}
	
	protected void setTryUnequip(boolean value) {
		modifyState(this.getClass(), "unequip", value);
	}
	
	public MyRelic(String id, RelicTier tier, LandingSound sfx) {
		super(TestMod.makeID(id), new Texture(Gdx.files.internal(TestMod.relicIMGPath(id))), tier, sfx);
	}
	
	public void show() {
		if (!show)
			return;
		flash();
	    AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, this));
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
	
}