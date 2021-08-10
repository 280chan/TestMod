package relics;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import utils.MiscMethods;

public class Charity extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "Charity";
	
	public Charity() {
		super(ID, RelicTier.SHOP, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
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