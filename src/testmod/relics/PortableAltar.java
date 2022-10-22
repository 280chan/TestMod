package testmod.relics;

import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import testmod.mymod.TestMod;
import testmod.relicsup.AbstractUpgradedRelic;

public class PortableAltar extends AbstractTestRelic {
	public static final String SAVE_NAME = "maxHPLost";
	
	public static int maxHPLost;
	private int turn = -1;
	
	public static void load(int loadValue) {
		maxHPLost = loadValue;
	}
	
	private static void saveMaxHPLost() {
		TestMod.save(SAVE_NAME, maxHPLost);
	}
	
	public static void reset() {
		maxHPLost = 0;
		saveMaxHPLost();
	}
	
	private void updateCounter() {
		this.counter = maxHPLost / 5;
	}
	
	public int getHPLost() {
		return maxHPLost;
	}
	
	private void updateMaxHPLost(int preMaxHP, int toLose) {
		int actual = p().maxHealth - preMaxHP;
		if (preMaxHP <= toLose) {
			toLose = preMaxHP - 1;
		}
		toLose = Math.max(toLose, actual);
		maxHPLost += toLose;
		updateCounter();
		TestMod.info("最大生命累计降低:" + maxHPLost + ", 当前计数:" + counter);
	}
	
	public void loseMaxHP(int toLose) {
		int tempMaxHP = p().maxHealth;
		p().decreaseMaxHealth(toLose);
		updateMaxHPLost(tempMaxHP, toLose);
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!isActive)
			return;
		this.counter = 0;
		reset();
	}
	
	public void onUnequip() {
		if (!isActive)
			return;
		if (!this.hasStack(AbstractUpgradedRelic.class.getCanonicalName(), "bossObtainLogic")) {
			p().increaseMaxHp(maxHPLost, true);
		}
	}
	
	public void atBattleStart() {
		if (!isActive)
			return;
		this.turn = 0;
		if (counter > 0) {
			this.show();
			this.att(apply(p(), new StrengthPower(p(), counter)));
			this.att(apply(p(), new DexterityPower(p(), counter)));
			this.att(apply(p(), new PlatedArmorPower(p(), counter)));
		}
	}
	
	public void atTurnStart() {
		if (!isActive)
			return;
		this.turn++;
		if (this.turn <= this.counter) {
			TestMod.info("turn= " + this.turn);
			return;
		}
		this.show();
		this.att(new LoseHPAction(p(), p(), 1));
	}
	
	public void onEnterRoom(final AbstractRoom room) {
		if (!isActive)
			return;
		saveMaxHPLost();
		this.show();
		this.loseMaxHP(1);
		this.turn = -1;
	}
	
	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.actNum < 2;
	}
	
}