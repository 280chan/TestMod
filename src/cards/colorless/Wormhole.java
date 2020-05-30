
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.WormholeAction;

public class Wormhole extends CustomCard {
    public static final String ID = "Wormhole";
    public static final String NAME = "虫洞";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "从抽牌堆、弃牌堆、手牌的所有牌中选出一张牌将其打出并将其 消耗 。";
    public static final String UPGRADED_DESCRIPTION = "从抽牌堆、弃牌堆、手牌的所有牌中选出一张牌将其打出。如果是 诅咒 或 状态 牌，将其 消耗 。";
    private static final int COST = 1;

    public Wormhole() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.RARE, CardTarget.ENEMY);
        this.exhaust = false;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        CardGroup g = new CardGroup(CardGroupType.UNSPECIFIED);
        g.group.addAll(p.drawPile.group);
        g.group.addAll(p.hand.group);
        g.group.addAll(p.discardPile.group);
        g.removeCard(this);
        for (AbstractCard c : p.hand.group)
        	c.beginGlowing();
        System.out.println("虫洞: Cardgroup大小=" + g.size());
        this.addToBot(new WormholeAction(g, m, !this.upgraded));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();//改名，其实就是多个+
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
        }
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}