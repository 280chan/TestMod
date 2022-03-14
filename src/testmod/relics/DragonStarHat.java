package testmod.relics;

import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;

import testmod.mymod.TestMod;

public class DragonStarHat extends AbstractTestRelic {
	public static final String ID = "DragonStarHat";
	public static final String SAVE_NAME = "HatMaxStr";
	public int maxValue = 0;
	
	private void save() {
		TestMod.save(SAVE_NAME, maxValue);
	}
	
	private void load(int maxValue) {
		this.maxValue = maxValue;
	}
	
	public static void loadValue(int maxValue) {
		if (AbstractDungeon.player.hasRelic(TestMod.makeID(ID)))
			((DragonStarHat) (AbstractDungeon.player.getRelic(TestMod.makeID(ID)))).load(maxValue);
	}
	
	public static void resetValue() {
		TestMod.save(SAVE_NAME, 0);
	}
	
	public DragonStarHat() {
		super(RelicTier.UNCOMMON, LandingSound.FLAT, BAD);
	}
	
	public void updateDescription(PlayerClass c) {
		this.tips.clear();
		String tmp = this.DESCRIPTIONS[0] + this.DESCRIPTIONS[1] + this.maxValue + this.DESCRIPTIONS[2] + this.counter
				+ this.DESCRIPTIONS[3];
		this.tips.add(new PowerTip(this.name, tmp));
		initializeTips();
	}
	
	public void onEquip() {
		this.counter = 0;
	}
	
	public void atPreBattle() {
		if (this.counter > 0) {
			this.show();
			this.addToTop(apply(p(), new StrengthPower(p(), this.counter)));
			if (--this.counter == 0)
				this.stopPulse();
		}
    }
	
	public void onRest() {
	    flash();
	    this.counter = ++this.maxValue;
	    this.beginLongPulse();
    }
	
	public void onEnterRestRoom() {
		if (TestMod.hasSaveData("HatMaxStr"))
			this.maxValue = TestMod.getInt("HatMaxStr");
		/*else
			this.save();*/
	    if ((this.counter = this.maxValue) > 0)
	    	this.beginLongPulse();
	}
	
	public void onEnterRoom(final AbstractRoom room) {
		if (!(room instanceof RestRoom || this.maxValue == TestMod.getInt("HatMaxStr")))
			save();
    }
	
	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.actNum < 2;
	}
	
}