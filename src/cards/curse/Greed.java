
package cards.curse;

import basemod.abstracts.*;
import mymod.TestMod;
import powers.GreedPower;
import relics.Prudence;
import relics.Sins;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;

public class Greed extends CustomCard {
    public static final String ID = "Greed";
    public static final String NAME = "贪婪";
    public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = " 不能被打出 。抽到这张牌的回合结束时，每有一点 能量 剩余，下回合少抽一张牌；每有一张牌剩余，下回合减少一点 能量 。";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
    private static final int COST = -2;//卡牌费用

    public Greed() {
    	super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.CURSE, CardColor.CURSE, CardRarity.SPECIAL, CardTarget.NONE);
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
	}
    
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
    	return p.hasRelic(Prudence.ID) || p.hasRelic("Blue Candle");
	}

	public void triggerWhenDrawn() {
		AbstractPlayer p = AbstractDungeon.player;
		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new GreedPower(p, 1), 1));
	}
    
    public AbstractCard makeCopy() {
		if (Sins.isObtained())
	        return new Greed();
		return Sins.copyCurse();
    }//复制卡牌后复制的卡，如果卡组里有复制卡牌的卡每张卡都要有这个

    public void upgrade() {
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}