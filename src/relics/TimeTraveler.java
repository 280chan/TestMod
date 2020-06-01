package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import mymod.TestMod;
import utils.MiscMethods;

public class TimeTraveler extends MyRelic implements MiscMethods {
	public static final String ID = "TimeTraveler";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "每回合开始获得 [R] 。你初始拥有 #b100 点(最大)理性值。每次 #ySL 损失 #r50% 当前理性值，每次战斗胜利恢复 #b5 点理性值，每次休息恢复 #b10 点理性值。当理性值低于 #b20 时每次战斗结束失去 #r10 点生命上限。当理性值低于 #b10 时每回合开始失去 #r1 点生命。";
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
		System.out.println("san已重置为100");
	}
	
	private static void save(int value) {
		TestMod.saveVariable("san", value);
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
			System.out.println("san: " + relic.counter);
		} else {
			System.out.println("玩家没有时间旅行者或新开游戏地图");
			reset();
		}
	}
	
	public TimeTraveler() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.BOSS, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		if (AbstractDungeon.player != null) {
			return setDescription(AbstractDungeon.player.chosenClass);
		}
		return setDescription(null);
	}

	private String setDescription(PlayerClass c) {
		return this.setDescription(c, this.DESCRIPTIONS[0], this.DESCRIPTIONS[1]);
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, setDescription(c)));
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
		TestMod.setActivity(this);
		if (!this.isActive)
			return;
		this.counter = 100;
		AbstractDungeon.player.energy.energyMaster += 1;
		relic = this;
    }
	
	public void onUnequip() {
		if (!this.isActive)
			return;
		AbstractDungeon.player.energy.energyMaster -= 1;
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