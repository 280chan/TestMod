package testmod.relicsup;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.vfx.ObtainKeyEffect;
import testmod.mymod.TestMod;
import testmod.relics.DragonStarHat;

public class DragonStarHatUp extends AbstractUpgradedRelic {
	
	public String getUpdatedDescription() {
		return this.isObtained ? this.DESCRIPTIONS[0] + this.DESCRIPTIONS[1] + this.counter + this.DESCRIPTIONS[2]
				: this.DESCRIPTIONS[0];
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.isActive)
			this.counter = 0;
		else
			this.counter = Math.max(0, this.relicStream(DragonStarHatUp.class).findFirst().orElse(this).counter);
		if (TestMod.hasSaveData(DragonStarHat.SAVE_NAME))
			this.counter += Math.max(0, TestMod.getInt(DragonStarHat.SAVE_NAME));
	}
	
	public void atPreBattle() {
		if (this.counter > 0) {
			this.show();
			this.att(apply(p(), new StrengthPower(p(), this.counter)));
			if (!this.isActive || relicStream(DragonStarHat.class).count() == 0)
				return;
			this.att(apply(p(), new StrengthPower(p(), relicStream(DragonStarHat.class).mapToInt(this::count).sum())));
		}
	}
	
	private int count(DragonStarHat r) {
		r.show();
		return r.counter = this.counter;
	}
	
	private void incrementCounter(DragonStarHatUp r) {
		r.counter++;
		r.updateDescription();
	}
	
	public void onRest() {
		if (this.isActive) {
			this.relicStream(DragonStarHatUp.class).forEach(this::incrementCounter);
		}
		AbstractDungeon.topLevelEffects.add(new ObtainKeyEffect(ObtainKeyEffect.KeyColor.RED));
		flash();
		this.stopPulse();
	}
	
	public void onEnterRestRoom() {
		this.beginLongPulse();
	}
	
	public void onEnterRoom(final AbstractRoom room) {
		if (room instanceof RestRoom)
			return;
		this.stopPulse();
	}
	
}