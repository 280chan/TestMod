package relics;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import mymod.TestMod;

public class Charity extends AbstractTestRelic{
	
	public static final String ID = "Charity";
	
	public Charity() {
		super(ID, RelicTier.SHOP, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void onGainGold() {
		AbstractPlayer p = AbstractDungeon.player;
		if (p.gold > 0)
			p.gold--;
		else {
			TestMod.info("Charity: WHY IS PLAYER'S GOLD NOT POSITIVE AFTER GAINING GOLD???");
		}
		p.increaseMaxHp(1, true);
		this.show();
    }
	
	public boolean canSpawn() {
		return (Settings.isEndless) || (AbstractDungeon.floorNum <= 48);
	}
	
}