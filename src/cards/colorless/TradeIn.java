
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.TradeInAction;

import com.megacrit.cardcrawl.dungeons.*;

public class TradeIn extends CustomCard {
    public static final String ID = "TradeIn";
    public static final String NAME = "以旧换新";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = " 消耗 任意张手牌。从抽牌堆中选择等量的牌放入手牌，其耗能在本回合降低 !M! 且打出时 消耗 。 消耗 。 虚无 。";
    private static final int COST = 1;//卡牌费用
    private static final int BASE_MGC = 1;

    public TradeIn() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.RARE, CardTarget.NONE);
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
        this.exhaust = true;
        this.isEthereal = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new TradeInAction(p, this.magicNumber));//将手牌中某张非技能卡复制magicNumber次
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}