
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;
import powers.ReflectPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;

public class Reflect extends CustomCard {
    public static final String ID = "Reflect";
    public static final String NAME = "反射";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "在这个回合，你的下 !M! 张非 诅咒 、 状态 的牌会打出两次。";
    private static final int COST = 1;
    private static final int BASE_MGC = 1;

    public Reflect() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.RARE, CardTarget.SELF);
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new ReflectPower(p, this.magicNumber), this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }
}