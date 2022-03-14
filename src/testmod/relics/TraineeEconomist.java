package testmod.relics;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;

public class TraineeEconomist extends AbstractTestRelic {
	private static final int DELTA_BONUS = 5;
	private static final int DELTA_PRICE = 5;
	private static final double PERCENTAGE = 100.0;
	private boolean used = false;
	//private boolean useCourier = false;
	
	public TraineeEconomist() {
		super(RelicTier.RARE, LandingSound.SOLID);
	}
	
	public String getUpdatedDescription() {
		return (this.counter < 1) ? DESCRIPTIONS[0]
				: DESCRIPTIONS[0] + DESCRIPTIONS[1] + goldRatePercent() + DESCRIPTIONS[2] + toPercent(this.priceRate())
						+ DESCRIPTIONS[3];
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
		this.counter += m.type == EnemyType.BOSS ? 3 : (m.type == EnemyType.ELITE ? 2 : 1);
		this.updateDescription(AbstractDungeon.player.chosenClass);
    }
	
	public double gainGold(double amount) {
		this.flash();
		return amount * gainGoldRate();
	}
	
	private void addDiscount() {
		AbstractDungeon.shopScreen.applyDiscount(this.priceRate(), true);
		if (AbstractDungeon.player.hasRelic("The Courier")) {
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
		if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() instanceof ShopRoom && !this.used) {
			this.flash();
			this.beginLongPulse();
			this.addDiscount();
			this.used = true;
		}
	}
	
	public void onEnterRoom(AbstractRoom room) {
		this.used = false;
		if (room instanceof ShopRoom) {
			this.flash();
			this.beginLongPulse();
		} else {
			this.stopPulse();
		}
	}

	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.actNum < 3;
	}

}