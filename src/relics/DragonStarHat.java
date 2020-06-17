package relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;

import mymod.TestMod;

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
		super(ID, RelicTier.UNCOMMON, LandingSound.FLAT);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void updateDescription(PlayerClass c) {
		this.tips.clear();
		String tmp = this.DESCRIPTIONS[0] + this.DESCRIPTIONS[1] + this.maxValue + this.DESCRIPTIONS[2] + this.counter + this.DESCRIPTIONS[3];
	    this.tips.add(new PowerTip(this.name, tmp));
	    initializeTips();
	}
	
	public void onEquip() {
		this.counter = 0;
	}
	
	public void atPreBattle() {
		if (this.counter > 0) {
			this.show();
			this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, this.counter), this.counter));
			this.counter--;
			if (this.counter == 0)
				this.stopPulse();
		}
    }
	
	public void onRest() {
	    flash();
	    this.maxValue++;
	    this.counter = this.maxValue;
	    this.beginLongPulse();
    }
	
	public void onEnterRestRoom() {
		if (TestMod.config.has("HatMaxStr"))
			this.maxValue = TestMod.config.getInt("HatMaxStr");
		/*else
			this.save();*/
	    this.counter = this.maxValue;
	    if (this.counter > 0)
	    	this.beginLongPulse();
	}
	
	public void onEnterRoom(final AbstractRoom room) {
		if (!(room instanceof RestRoom))
			if (this.maxValue != TestMod.config.getInt("HatMaxStr"))
				save();
    }
	
	public boolean canSpawn() {
		if (!Settings.isEndless && AbstractDungeon.actNum > 1)
			return false;
		return true;
	}
	
}