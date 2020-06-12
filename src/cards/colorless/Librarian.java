
package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;

import actions.LibrarianAction;

public class Librarian extends AbstractTestCard {
    public static final String ID = "Librarian";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    private static final int COST = -1;
    private static final int BASE_MGC = 1;

    public Librarian() {
        super(ID, NAME, COST, DESCRIPTION, CardType.SKILL, CardRarity.RARE, CardTarget.NONE);
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