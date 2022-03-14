package testmod.relics;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class Charity extends AbstractTestRelic {
	
	public Charity() {
		super(RelicTier.SHOP, LandingSound.MAGICAL);
	}
	
	public double gainGold(double amount) {
		AbstractDungeon.player.increaseMaxHp(1, true);
		this.show();
		return Math.max(0, amount - 1);
	}
	
	public boolean canSpawn() {
		return (Settings.isEndless) || (AbstractDungeon.floorNum <= 48);
	}
}