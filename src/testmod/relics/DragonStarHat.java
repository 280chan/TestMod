package testmod.relics;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;

import testmod.mymod.TestMod;
import testmod.relicsup.DragonStarHatUp;

public class DragonStarHat extends AbstractTestRelic {
	public static final String SAVE_NAME = "HatMaxStr";
	public static int maxValue = 0;
	
	private void save() {
		TestMod.save(SAVE_NAME, maxValue);
	}
	
	private void load(int maxValue) {
		DragonStarHat.maxValue = maxValue;
	}
	
	public static void loadValue(int maxValue) {
		MISC.relicStream(DragonStarHat.class).limit(1).forEach(r -> r.load(maxValue));
	}
	
	public static void resetValue() {
		TestMod.save(SAVE_NAME, 0);
	}
	
	public String getUpdatedDescription() {
		return this.isObtained ? this.DESCRIPTIONS[0] + this.DESCRIPTIONS[1] + maxValue + this.DESCRIPTIONS[2]
				+ this.counter + this.DESCRIPTIONS[3] : this.DESCRIPTIONS[0];
	}
	
	public void onEquip() {
		this.counter = 0;
	}
	
	public void atPreBattle() {
		if (this.counter > 0) {
			this.show();
			this.att(apply(p(), new StrengthPower(p(), this.counter)));
			if (--this.counter == 0)
				this.stopPulse();
		}
	}
	
	public void onRest() {
		if (!this.isActive || this.relicStream(DragonStarHatUp.class).count() > 0)
			return;
		flash();
		maxValue += this.relicStream(DragonStarHat.class).count();
		this.relicStream(DragonStarHat.class).forEach(r -> r.counter = maxValue);
		this.beginLongPulse();
	}
	
	public void onEnterRestRoom() {
		if (this.isActive && TestMod.hasSaveData("HatMaxStr")) {
			maxValue = TestMod.getInt("HatMaxStr");
			this.relicStream(DragonStarHat.class).forEach(r -> r.counter = maxValue);
		}
		if ((this.counter = maxValue) > 0)
			this.beginLongPulse();
	}
	
	public void onEnterRoom(final AbstractRoom room) {
		if (this.isActive && !(room instanceof RestRoom || maxValue == TestMod.getInt("HatMaxStr")))
			save();
	}
	
	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.actNum < 2;
	}
	
}