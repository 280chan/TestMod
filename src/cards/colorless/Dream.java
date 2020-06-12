
package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.DreamAction;

import com.megacrit.cardcrawl.localization.CardStrings;

public class Dream extends AbstractTestCard {
    public static final String ID = "Dream";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final String UPGRADED_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    private static final int COST = 3;

    public Dream() {
        super(ID, NAME, COST, DESCRIPTION, CardType.SKILL, CardRarity.RARE, CardTarget.NONE);
        this.exhaust = true;
        this.isEthereal = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new DreamAction());
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