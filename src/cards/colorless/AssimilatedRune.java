
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;
import powers.AssimilatedRunePower;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.AssimilatedRuneApplicationAction;

import com.megacrit.cardcrawl.dungeons.*;

public class AssimilatedRune extends CustomCard {
    public static final String ID = "AssimilatedRune";
    public static final String NAME = "同化符文";
    public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "在本回合内，将当前手牌中所有牌的基础伤害、基础格挡提升到当前手牌中的最高值。";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
    public static final String UPGRADED_DESCRIPTION = "在本回合内，将当前手牌中所有牌的基础伤害、基础格挡提升到你所有牌中的最高值。";
    private static final int COST = 1;
    private static final int BASE_MGC = 1;

    public AssimilatedRune() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.RARE, CardTarget.SELF);
        this.baseMagicNumber = BASE_MGC;//特殊值，一般用来叠BUFF层数。和下一行连用。
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	AbstractDungeon.actionManager.addToBottom(new AssimilatedRuneApplicationAction(new AssimilatedRunePower(p, this.magicNumber, this.upgraded)));
    }
    
    public AbstractCard makeCopy() {
        return new AssimilatedRune();
    }//复制卡牌后复制的卡，如果卡组里有复制卡牌的卡每张卡都要有这个

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
        }
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}