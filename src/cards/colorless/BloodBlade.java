
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;
import powers.BloodBladePower;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;

public class BloodBlade extends CustomCard {
    public static final String ID = "BloodBlade";
    public static final String NAME = "血气之刃";
    public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "提升已失去生命/最大生命的攻击伤害。失去生命时，提升失去量/最大生命的攻击伤害。加成之间相加。";
    public static final String UPGRADED_DESCRIPTION = "提升已失去生命/最大生命的攻击伤害。失去生命时，提升失去量/最大生命的攻击伤害。加成之间相乘。";
    private static final int COST = 1;//卡牌费用

    public BloodBlade() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.RARE, CardTarget.SELF);
    }
    
    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	if (BloodBladePower.hasThis(this.upgraded)) {
    		BloodBladePower.getThis(this.upgraded).onFirstGain();
    	} else {
    		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new BloodBladePower(p, this.upgraded)));
    	}
    }
    
    public AbstractCard makeCopy() {
        return new BloodBlade();
    }//复制卡牌后复制的卡，如果卡组里有复制卡牌的卡每张卡都要有这个

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();//改名，其实就是多个+
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
        }
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}