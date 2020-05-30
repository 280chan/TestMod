package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mymod.TestMod;
import powers.InfectionPower;
import powers.InfectionSourcePower;

public class InfectionSource extends MyRelic {
	
	public static final String ID = "InfectionSource";
	public static final String IMG = TestMod.relicIMGPath(ID);
	
	public static final String DESCRIPTION = "你的攻击伤害降低 #r50% 。被攻击击中的敌人获得相同点数的 #y中毒 ，但只会受到 #b1 点伤害。";//遗物效果的文本描叙。
	
	public InfectionSource() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void atBattleStart() {
		flash();
		AbstractPlayer p = AbstractDungeon.player;
		AbstractDungeon.actionManager.addToTop(new RelicAboveCreatureAction(p, this));
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!m.isDead && !m.isDying) {
				AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new InfectionPower(m)));
			}
		}
		AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(p, p, new InfectionSourcePower(p)));
    }//触发时机：当玩家战斗开始时，在第一轮抽牌之后。(参考金刚杵、缩放仪)
	
	public void atTurnStart() {
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!m.isDead && !m.isDying && !m.hasPower("InfectionPower")) {
				AbstractPlayer p = AbstractDungeon.player;
				AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new InfectionPower(m)));
			}
		}
    }

}