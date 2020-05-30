
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;
import powers.ChaoticCorePower;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.defect.IncreaseMaxOrbAction;

public class ChaoticCore extends CustomCard {
    public static final String ID = "ChaoticCore";
    public static final String NAME = "混沌原核";
    public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "获得1个充能球栏位。每受到一次伤害，获得1个充能球栏位，已满则改为触发下一个效果。每造成一次攻击伤害， 生成  !M! 个随机充能球。";
    private static final int COST = 3;
    private static final int BASE_MGC = 1;
    private static final int ORB_SLOT = 1;

    public ChaoticCore() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.RARE, CardTarget.SELF);
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	AbstractDungeon.actionManager.addToBottom(new IncreaseMaxOrbAction(ORB_SLOT));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new ChaoticCorePower(p, this.magicNumber), this.magicNumber));
    }
    
    public AbstractCard makeCopy() {
        return new ChaoticCore();
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }
}