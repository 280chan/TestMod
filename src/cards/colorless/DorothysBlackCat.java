
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;
import powers.DorothysBlackCatPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;

public class DorothysBlackCat extends CustomCard {
    public static final String ID = "DorothysBlackCat";
    public static final String NAME = "桃乐丝的黑猫";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "每当你对敌人造成失去生命以外类型的伤害时，使所有敌人均摊失去总共 !M! %其数值的生命。";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
    private static final int COST = 3;
    private static final int BASE_MGC = 75;

    public DorothysBlackCat() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.RARE, CardTarget.SELF);
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
    }
    
    public void use(final AbstractPlayer p, final AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new DorothysBlackCatPower(p, this.magicNumber), this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(25);
        }
    }
}