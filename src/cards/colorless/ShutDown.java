
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;
import powers.ShutDownPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;

public class ShutDown extends CustomCard {
    public static final String ID = "ShutDown";
    public static final String NAME = "关机";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "每回合将1张重启放入手牌。";
    public static final String UPGRADED_DESCRIPTION = "每回合将1张重启+放入手牌。";
    private static final int COST = 1;//卡牌费用

    public ShutDown() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.RARE, CardTarget.SELF);
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new ShutDownPower(p, this.upgraded), 1));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();//改名，其实就是多个+
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
        }
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}