package relics;

import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

public class VentureCapital extends AbstractTestRelic {
	public static final String ID = "VentureCapital";
	private static final int LOSE_GOLD_AMOUNT = 1;
	private static final double PERCENTAGE = 100.0;
	
	public VentureCapital() {
		super(ID, RelicTier.RARE, LandingSound.CLINK);
	}
	
	public String getUpdatedDescription() {
		return this.counter < 1 ? DESCRIPTIONS[0]
				: DESCRIPTIONS[0] + DESCRIPTIONS[1] + goldRatePercent() + DESCRIPTIONS[2];
	}
	
	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}

	private String goldRatePercent() {
		return 100 + this.counter + "";
	}
	
	private double gainGoldRate() {
		return 1 + this.counter / PERCENTAGE;
	}
	
	public void onEquip() {
		this.counter = 0;
		if (AbstractDungeon.player.gold > 0)
			this.increaseCounter(AbstractDungeon.player.gold);
		AbstractDungeon.player.loseGold(AbstractDungeon.player.gold);
    }
	
	private void increaseCounter(int gold) {
		this.counter += gold;
		this.updateDescription(AbstractDungeon.player.chosenClass);
	}
	
	public double gainGold(double amount) {
		double tmp = amount * gainGoldRate();
		if (tmp >= LOSE_GOLD_AMOUNT) {
			tmp -= LOSE_GOLD_AMOUNT;
			this.increaseCounter(LOSE_GOLD_AMOUNT);
			this.flash();
		}
		return tmp;
	}

	public boolean canSpawn() {
		return (Settings.isEndless) || (AbstractDungeon.actNum < 3);
	}

}