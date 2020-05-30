
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.LibrarianAction;

public class Librarian extends CustomCard {
    public static final String ID = "Librarian";
    public static final String NAME = "图书管理员";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "从X张随机无色牌选择 !M! 张加入手牌，其耗能在本场战斗降为0。 消耗 。";
    public static final String UPGRADE_DESCRIPTION = "从X张随机无色牌选择 !M! 张加入手牌，其耗能在本场战斗降为0。接下来第X次回合开始前胜利，将其加入牌组。 消耗 。";
    private static final int COST = -1;
    private static final int BASE_MGC = 1;

    public Librarian() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.RARE, CardTarget.NONE);
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
        this.exhaust = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new LibrarianAction(p, this.upgraded, this.freeToPlayOnce, this.energyOnUse, this.magicNumber));
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.rawDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}