
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;
import powers.RecapPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;

public class Recap extends CustomCard {
    public static final String ID = "Recap";
    public static final String NAME = "前情提要";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "每回合开始，打出你上一回合打出的最后 !M! 张牌一次。(限玩家打出)";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
    private static final int COST = 2;
    private static final int BASE_MGC = 1;

    public Recap() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.RARE, CardTarget.SELF);
        this.baseMagicNumber = BASE_MGC;//特殊值，一般用来叠BUFF层数。和下一行连用。
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new RecapPower(p, this.magicNumber), this.magicNumber));
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(1);
        }
    }
}