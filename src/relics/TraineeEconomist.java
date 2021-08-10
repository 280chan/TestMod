package relics;

import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import utils.MiscMethods;

public class TraineeEconomist extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "TraineeEconomist";
	private static final int DELTA_BONUS = 5;
	private static final int DELTA_PRICE = 5;
	private static final double PERCENTAGE = 100.0;
	private boolean used = false;
	//private boolean useCourier = false;
	
	public TraineeEconomist() {
		super(ID, RelicTier.RARE, LandingSound.SOLID);
	}
	
	public String getUpdatedDescription() {
		if (this.counter < 1)
			return DESCRIPTIONS[0];
		return DESCRIPTIONS[0] + DESCRIPTIONS[1] + goldRatePercent() + DESCRIPTIONS[2]
				+ toPercent(this.priceRate()) + DESCRIPTIONS[3];
	}
	
	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}

	private static String toPercent(double input) {
		return ((int) (input * 10000)) / PERCENTAGE + "";
	}

	private String goldRatePercent() {
		return 100 + DELTA_BONUS * this.counter + "";
	}
	
	private double gainGoldRate() {
		return 1 + DELTA_BONUS * this.counter / PERCENTAGE;
	}
	
	private float priceRate() {
		return (float) Math.pow((100 - DELTA_PRICE) / PERCENTAGE, this.counter);
	}
	
	public void onEquip() {
		this.counter = 0;
    }
	
	public void onMonsterDeath(AbstractMonster m) {
		int delta = 1;
		if (m.type == EnemyType.ELITE)
			delta = 2;
		else if (m.type == EnemyType.BOSS)
			delta = 3;
		this.counter += delta;
		this.updateDescription(AbstractDungeon.player.chosenClass);
    }
	
	public double gainGold(double amount) {
		return amount * gainGoldRate();
	}
	
	private void addDiscount() {
		if ((!AbstractDungeon.player.hasRelic("The Courier"))) {
			AbstractDungeon.shopScreen.applyDiscount(this.priceRate(), true);
		} else {
			AbstractDungeon.shopScreen.applyDiscount(this.priceRate(), true);
			this.used = false;
		}
	}

	public void onSpendGold() {
		if ((AbstractDungeon.player != null) && (AbstractDungeon.player.hasRelic("The Courier"))) {
			//this.useCourier = false;
		}
	}

	public void update() {
		super.update();
		if (!this.isObtained)
			return;
		if ((AbstractDungeon.currMapNode != null) && ((AbstractDungeon.getCurrRoom() instanceof ShopRoom))
				&& (!this.used)) {
			this.flash();
			this.beginLongPulse();
			this.addDiscount();
			this.used = true;
		}
	}
	
	public void onEnterRoom(AbstractRoom room) {
		this.used = false;
		if ((room instanceof ShopRoom)) {
			this.flash();
			this.beginLongPulse();
		} else {
			this.stopPulse();
		}
	}

	public boolean canSpawn() {
		return (Settings.isEndless) || (AbstractDungeon.floorNum <= 48);
	}

}