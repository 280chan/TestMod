
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;
import utils.MiscMethods;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;

public class HeadAttack extends CustomCard implements MiscMethods {
    public static final String ID = "HeadAttack";
    public static final String NAME = "头部攻击";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "使敌人意图改变为其下一回合的意图。抽 !M! 张牌。 虚无 。";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
    private static final int COST = 0;//卡牌费用
    private static final int BASE_MGC = 1;
    
    public HeadAttack() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.RARE, CardTarget.ENEMY);
        this.isEthereal = true;
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.rollIntent(m);
    	AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, this.magicNumber));
    }
    
    public AbstractCard makeCopy() {
        return new HeadAttack();
    }//复制卡牌后复制的卡，如果卡组里有复制卡牌的卡每张卡都要有这个

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }
}