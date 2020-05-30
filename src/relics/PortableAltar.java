package relics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
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

public class PortableAltar extends MyRelic{
	private static final Logger logger = LogManager.getLogger(TestMod.class.getName());
	public static final String ID = "PortableAltar";
	public static final String IMG = TestMod.relicIMGPath(ID);
	
	public static final String DESCRIPTION = "战斗中每回合失去 #r1 点生命。每进入一个房间，失去 #r1 点最大生命。拾取后每通过该遗物失去 #r5 点最大生命，在战斗开始时获得 #b1 点 力量 ， #b1 点 敏捷 ， #b1 层 多层护甲 。";//遗物效果的文本描叙。
	
	public static int maxHPLost;
	
	private void saveMaxHPLost() {
		TestMod.saveVariable("maxHPLost", maxHPLost);
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
		logger.info("最大生命累计降低:" + maxHPLost + ", 当前计数:" + counter);
	}
	
	public void loseMaxHP(int toLose) {
		int tempMaxHP = AbstractDungeon.player.maxHealth;
		AbstractDungeon.player.decreaseMaxHealth(toLose);
		updateMaxHPLost(tempMaxHP, toLose);
	}
	
	public PortableAltar() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.BOSS, LandingSound.HEAVY);
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
		if (counter > 0) {
			this.show();
			AbstractPlayer p = AbstractDungeon.player;
			AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(p, p, new StrengthPower(p, counter), counter));
			AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(p, p, new DexterityPower(p, counter), counter));
			AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(p, p, new PlatedArmorPower(p, counter), counter));
		}
	}
	
	public void atTurnStart() {
		if (!isActive)
			return;
		this.show();
		AbstractDungeon.actionManager.addToTop(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, 1));
	}
	
	public void onEnterRoom(final AbstractRoom room) {
		if (!isActive)
			return;
		this.saveMaxHPLost();
		this.show();
		this.loseMaxHP(1);
    }
	
	public boolean canSpawn() {
		if (!Settings.isEndless && AbstractDungeon.actNum > 1) {
			return false;
		}
		return true;
	}
	
}