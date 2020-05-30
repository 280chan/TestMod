
package deprecated.cards.colorless;

import basemod.abstracts.*;
import deprecated.powers.GhostWarHorsePower;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;

/**
 * @deprecated
 */
public class GhostWarHorse extends CustomCard {
    public static final String ID = "GhostWarHorse";
    public static final String NAME = "幽灵战马";
    public static final String IMG = "resources/images/relic1.png";
    public static final String DESCRIPTION = "每当你打出能力:获得1层幽魂形态；技能:如果有幽魂形态，降低1层并减少1点敏捷；攻击:获得幽魂形态层数点敏捷。";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
    private static final String UPGRADED_DESCRIPTION = "每当你打出能力:获得1层幽魂形态；技能:如果有幽魂形态，降低1层并减少1点敏捷；攻击:获得幽魂形态层数+1点敏捷。";
    private static final int COST = 3;//卡牌费用
    private static final int BASE_MGC = 0;

    public GhostWarHorse() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.RARE, CardTarget.SELF);
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new GhostWarHorsePower(p, this.magicNumber), this.magicNumber));
    }
    
    public AbstractCard makeCopy() {
        return new GhostWarHorse();
    }//复制卡牌后复制的卡，如果卡组里有复制卡牌的卡每张卡都要有这个

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
        }
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}