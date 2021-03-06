package relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import mymod.TestMod;

public class PortableAltar extends AbstractTestRelic {
	public static final String ID = "PortableAltar";
	public static final String SAVE_NAME = "maxHPLost";
	
	private static int maxHPLost;
	private int turn = -1;
	
	public static void load(int loadValue) {
		maxHPLost = loadValue;
	}
	
	private void saveMaxHPLost() {
		TestMod.save("maxHPLost", maxHPLost);
	}
	
	public void reset() {
		this.counter = 0;
		maxHPLost = 0;
		this.saveMaxHPLost();
	}
	
	private void updateCounter() {
		this.counter = maxHPLost / 5;
	}
	
	public int getHPLost() {
		return maxHPLost;
	}
	
	private void updateMaxHPLost(int preMaxHP, int toLose) {
		if (preMaxHP <= toLose) {
			toLose = preMaxHP - 1;
		}
		maxHPLost += toLose;
		updateCounter();
		TestMod.info("最大生命累计降低:" + maxHPLost + ", 当前计数:" + counter);
	}
	
	public void loseMaxHP(int toLose) {
		int tempMaxHP = AbstractDungeon.player.maxHealth;
		AbstractDungeon.player.decreaseMaxHealth(toLose);
		updateMaxHPLost(tempMaxHP, toLose);
	}
	
	public PortableAltar() {
		super(ID, RelicTier.BOSS, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!isActive)
			return;
		this.reset();
	}
	
	public void onUnequip() {
		if (!isActive)
			return;
		AbstractDungeon.player.increaseMaxHp(maxHPLost, true);
    }
	
	public void atBattleStart() {
		if (!isActive)
			return;
		this.turn = 0;
		if (counter > 0) {
			this.show();
			AbstractPlayer p = AbstractDungeon.player;
			this.addToTop(new ApplyPowerAction(p, p, new StrengthPower(p, counter), counter));
			this.addToTop(new ApplyPowerAction(p, p, new DexterityPower(p, counter), counter));
			this.addToTop(new ApplyPowerAction(p, p, new PlatedArmorPower(p, counter), counter));
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
		this.addToTop(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, 1));
	}
	
	public void onEnterRoom(final AbstractRoom room) {
		if (!isActive)
			return;
		this.saveMaxHPLost();
		this.show();
		this.loseMaxHP(1);
		this.turn = -1;
    }
	
	public boolean canSpawn() {
		if (!Settings.isEndless && AbstractDungeon.actNum > 1) {
			return false;
		}
		return true;
	}
	
}