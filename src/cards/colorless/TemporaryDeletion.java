
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.TemporaryDeletionAction;

import com.megacrit.cardcrawl.dungeons.*;

public class TemporaryDeletion extends CustomCard {
    public static final String ID = "TemporaryDeletion";
    public static final String NAME = "临时删除";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "选择一张牌，将与其稀有度相同的所有牌从本场战斗移除。每回合开始将一张其稀有度的随机牌加入手牌。 虚无 。";
    private static final int COST = 2;

    public TemporaryDeletion() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.UNCOMMON, CardTarget.NONE);
        this.isEthereal = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	CardGroup g = new CardGroup(CardGroupType.UNSPECIFIED);
        g.group.addAll(p.drawPile.group);
        g.group.addAll(p.hand.group);
        g.group.addAll(p.discardPile.group);
        g.removeCard(this);
        for (AbstractCard c : p.hand.group)
        	c.stopGlowing();
        AbstractDungeon.actionManager.addToBottom(new TemporaryDeletionAction(g, p));//为自身增加magicNumber层多层护甲
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(1);
        }
    }
}