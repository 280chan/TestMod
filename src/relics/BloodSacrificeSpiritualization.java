package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

import actions.BloodSacrificeSpiritualizationSelectAction;
import mymod.TestMod;

public class BloodSacrificeSpiritualization extends MyRelic {
	public static final String ID = "BloodSacrificeSpiritualization";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "战斗开始时，可以选择失去 #b10% 最大生命(至少 #b1 )的生命，从你的所有牌中选择一张牌，将其对随机目标打出。若其可被 #y升级 则在打出前将其在本局游戏 #y升级 。";
	
	public BloodSacrificeSpiritualization() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void atBattleStart() {
		AbstractDungeon.actionManager.addToBottom(new BloodSacrificeSpiritualizationSelectAction(AbstractDungeon.player));
    }

	public boolean canSpawn() {
		if (!Settings.isEndless && AbstractDungeon.actNum > 1) {
			return false;
		}
		return true;
	}
	
}