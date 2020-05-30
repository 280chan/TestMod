package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import mymod.TestMod;

public class RealStoneCalender extends MyRelic{
	public static final String ID = "RealStoneCalender";
	public static final String IMG = TestMod.relicIMGPath(ID);
	
	public static final String DESCRIPTION = "每回合结束，对所有敌人造成回合数的平方的伤害。每 #b7 回合重置计算伤害的回合数。";//遗物效果的文本描叙。
	
	public RealStoneCalender() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.RARE, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atBattleStart() {
		this.counter = 0;
    }
	
	public void onPlayerEndTurn() {
		this.counter++;
		int temp = this.counter * this.counter;
		if (this.counter == 7)
			this.counter = 0;
		AbstractDungeon.actionManager.addToTop(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(temp, true), DamageInfo.DamageType.THORNS, AttackEffect.BLUNT_HEAVY));
    }
	
	public void onVictory() {
		this.counter = -1;
    }
}