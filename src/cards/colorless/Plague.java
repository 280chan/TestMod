
package cards.colorless;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.PlagueAction;
import cards.AbstractTestCard;

import com.megacrit.cardcrawl.localization.CardStrings;

public class Plague extends AbstractTestCard {
    public static final String ID = "Plague";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = -1;

    public Plague() {
        super(ID, NAME, COST, DESCRIPTION, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
        this.isEthereal = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new PlagueAction(p, this.freeToPlayOnce, this.energyOnUse));
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.isEthereal = false;
        }
    }
}