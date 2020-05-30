
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;
import powers.ReverberationPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;

public class Reverberation extends CustomCard {
    public static final String ID = "Reverberation";
    public static final String NAME = "残响";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "每当一张非 诅咒 、 状态 牌 消耗 时，触发其打出时的效果 !M! 次。 虚无 。";
    public static final String UPGRADED_DESCRIPTION = "每当一张非 诅咒 、 状态 牌 消耗 时，触发其打出时的效果 !M! 次。";
    private static final int COST = 3;
    private static final int BASE_MGC = 1;

    public Reverberation() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.RARE, CardTarget.SELF);
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
        this.isEthereal = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new ReverberationPower(p, this.magicNumber), this.magicNumber));//为自身增加magicNumber层多层护甲
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.isEthereal = false;
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
        }
    }
}