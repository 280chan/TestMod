package relics;

import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import mymod.TestMod;

public class TimeTraveler extends AbstractTestRelic {
	public static final String ID = "TimeTraveler";
	public static final String SAVE_NAME = "san";
	private static final int REST_SAN = 10;
	private static final int VICTORY_SAN = 5;
	private static final int MAX_HP_LOSS = 10;
	private static final int SAN_TO_LOSE_MAX_HP = 20;
	private static final int SL_SAN_LOSS_PERCENT = 50;
	private static final int SAN_TO_LOSE_HP_TURN_START = 10;
	private static AbstractRelic relic = null;
	private static int san;
	private static boolean saveLater = false;
	private static int saveRest;
	
	private static void reset() {
		save(100);
		TestMod.info("san已重置为100");
	}
	
	private static void save(int value) {
		TestMod.save(SAVE_NAME, value);
	}
	
	public static void load(int value) {
		san = value;
		loadGame();
	}
	
	private static void loadGame() {
		relic = AbstractDungeon.player.getRelic(TestMod.makeID(ID));
		if (relic != null) {
			relic.counter = san = san * (100 - SL_SAN_LOSS_PERCENT) / 100;
			save(san);
			TestMod.info("san: " + relic.counter);
		} else {
			TestMod.info("玩家没有时间旅行者或新开游戏地图");
			reset();
		}
	}
	
	public TimeTraveler() {
		super(ID, RelicTier.BOSS, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		return this.DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	private void tryDecreaseMaxHP() {
		if (!this.isActive)
			return;
		if (this.counter < SAN_TO_LOSE_MAX_HP)
			AbstractDungeon.player.decreaseMaxHealth(MAX_HP_LOSS);
	}
	
	private void tryChangeSan(int amount) {
		if (!this.isActive)
			return;
		this.counter += amount;
		if (this.counter > 100)
			this.counter = 100;
		else if (this.counter < 0)
			this.counter = 0;
	}
	
	public void atTurnStart() {
		if (!this.isActive)
			return;
		if (this.counter < SAN_TO_LOSE_HP_TURN_START)
			this.addToTop(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, 1));
	}
	
	public void onEquip() {
		AbstractDungeon.player.energy.energyMaster++;
		TestMod.setActivity(this);
		if (!this.isActive)
			return;
		this.counter = 100;
		relic = this;
    }
	
	public void onUnequip() {
		AbstractDungeon.player.energy.energyMaster--;
		if (!this.isActive)
			return;
		relic = null;
    }
	
	public void onVictory() {
		this.tryDecreaseMaxHP();
		this.tryChangeSan(VICTORY_SAN);
		save(this.counter);
    }
	
	public void onRest() {
		this.tryChangeSan(REST_SAN);
		saveLater = true;
		saveRest = this.counter;
    }

	public void onEnterRoom(final AbstractRoom room) {
		if (saveLater) {
			save(saveRest);
			saveLater = false;
		}
    }
}