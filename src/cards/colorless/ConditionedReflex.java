
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;
import powers.ConditionedReflexPower;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;

public class ConditionedReflex extends CustomCard {
    public static final String ID = "ConditionedReflex";
    public static final String NAME = "条件反射";
    public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "每失去一次生命，获得 !M! 层 柔韧 ，并在下回合开始时使其生效1次。";
    private static final int COST = 1;//卡牌费用
    private static final int BASE_MGC = 1;

    public ConditionedReflex() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.UNCOMMON, CardTarget.SELF);
        this.baseMagicNumber = BASE_MGC;//特殊值，一般用来叠BUFF层数。和下一行连用。
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new ConditionedReflexPower(p, this.magicNumber), this.magicNumber));
    }
    
    public AbstractCard makeCopy() {
        return new ConditionedReflex();
    }//复制卡牌后复制的卡，如果卡组里有复制卡牌的卡每张卡都要有这个

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();//改名，其实就是多个+
            this.upgradeMagicNumber(1);
        }
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}