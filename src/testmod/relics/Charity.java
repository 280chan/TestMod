package testmod.relics;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import testmod.relicsup.CharityUp;

public class Charity extends AbstractTestRelic {
	
	public Charity() {
		super(RelicTier.SHOP, LandingSound.MAGICAL);
	}
	
	public double gainGold(double amount) {
		this.show();
		if (!this.isActive || this.relicStream(CharityUp.class).count() > 0)
			return amount;
		p().increaseMaxHp((int) this.relicStream(Charity.class).count(), true);
		return Math.max(0, amount - 1);
	}
	
	public boolean canSpawn() {
		return (Settings.isEndless) || (AbstractDungeon.floorNum <= 48);
	}
}