
package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.TemporaryDeletionAction;

import com.megacrit.cardcrawl.localization.CardStrings;

public class TemporaryDeletion extends AbstractTestCard {
    public static final String ID = "TemporaryDeletion";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 2;

    public TemporaryDeletion() {
        super(ID, NAME, COST, DESCRIPTION, CardType.POWER, CardRarity.UNCOMMON, CardTarget.NONE);
        this.isEthereal = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	CardGroup g = new CardGroup(CardGroupType.UNSPECIFIED);
        g.group.addAll(p.drawPile.group);
        g.group.addAll(p.hand.group);
        g.group.addAll(p.discardPile.group);
        g.removeCard(this);
        for (AbstractCard c : p.hand.group)
        	c.beginGlowing();
        this.addToBot(new TemporaryDeletionAction(g, p));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(1);
        }
    }
}