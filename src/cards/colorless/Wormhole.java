
package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;

import actions.WormholeAction;

public class Wormhole extends AbstractTestCard {
    public static final String ID = "Wormhole";
	private static final CardStrings cardStrings = AbstractTestCard.Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final String UPGRADED_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    private static final int COST = 1;

    public Wormhole() {
        super(ID, NAME, COST, DESCRIPTION, CardType.SKILL, CardRarity.RARE, CardTarget.ENEMY);
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
            this.upgradeName();
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
        }
    }
}