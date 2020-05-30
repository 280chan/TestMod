
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.DreamAction;

import com.megacrit.cardcrawl.dungeons.*;

public class Dream extends CustomCard {
    public static final String ID = "Dream";
    public static final String NAME = "梦想";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "将地狱之刃、声东击西、白噪声、发现加入手牌，其耗能在本回合变为0。 消耗 。 虚无 。";
    public static final String UPGRADED_DESCRIPTION = "将地狱之刃、声东击西、白噪声、发现加入手牌，其耗能在本回合变为0。 虚无 。";
    private static final int COST = 3;

    public Dream() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.RARE, CardTarget.NONE);
        this.exhaust = true;
        this.isEthereal = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	AbstractDungeon.actionManager.addToBottom(new DreamAction());
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.exhaust = false;
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
        }
    }
}