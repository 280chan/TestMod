
package cards.curse;

import basemod.abstracts.*;
import mymod.TestMod;
import relics.Prudence;
import relics.Sins;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.*;

public class Gluttony extends CustomCard {
    public static final String ID = "Gluttony";
    public static final String NAME = "暴食";
    public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = " 不能被打出 。增加最大生命时，复制一张暴食到你的牌堆。这张牌被移除或转化时，降低20%最大生命。";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
    private static final int COST = -2;//卡牌费用

    public Gluttony() {
    	super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.CURSE, CardColor.CURSE, CardRarity.SPECIAL, CardTarget.NONE);
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    }
    
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
		return p.hasRelic(Prudence.ID) || p.hasRelic("Blue Candle");
	}
    
    public void onRemoveFromMasterDeck() {
		AbstractDungeon.player.decreaseMaxHealth(AbstractDungeon.player.maxHealth / 5);
    }
    
    public AbstractCard makeCopy() {
		if (Sins.isObtained())
	        return new Gluttony();
		return Sins.copyCurse();
    }

    public void upgrade() {
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}