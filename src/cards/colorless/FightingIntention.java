
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;
import powers.FightingIntentionPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;

public class FightingIntention extends CustomCard {
    public static final String ID = "FightingIntention";
    public static final String NAME = "战意";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "回合开始时，每个攻击意图的敌人使你获得 !M! 点 力量 ，使攻击意图的敌人失去 !M! 点 力量 。";
    private static final int COST = 3;
    private static final int BASE_MGC = 1;

    public FightingIntention() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.RARE, CardTarget.SELF);
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new FightingIntentionPower(p, this.magicNumber), this.magicNumber));//为自身增加magicNumber层多层护甲
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(2);
        }
    }
}