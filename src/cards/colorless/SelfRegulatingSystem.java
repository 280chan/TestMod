
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;
import powers.SelfRegulatingSystemPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;

public class SelfRegulatingSystem extends CustomCard {
    public static final String ID = "SelfRegulatingSystem";
    public static final String NAME = "自我调节系统";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "每回合获得的首个增益状态层数增加 !M! ，首个减益状态层数减少 !M! 。";
    public static final String UPGRADED_DESCRIPTION = "每回合获得的首个增益状态层数增加 !M! ，首个减益状态层数减少 !M! 。 固有 。";
    private static final int COST = 2;
    private static final int BASE_MGC = 1;

    public SelfRegulatingSystem() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.RARE, CardTarget.SELF);
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new SelfRegulatingSystemPower(p, this.magicNumber), this.magicNumber));//为自身增加magicNumber层多层护甲
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.isInnate = true;
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
        }
    }
}