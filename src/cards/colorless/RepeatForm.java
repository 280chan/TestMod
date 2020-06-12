
package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.RepeatFormAction;

import com.megacrit.cardcrawl.localization.CardStrings;

public class RepeatForm extends AbstractTestCard {
	public static final String ID = "RepeatForm";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
	private static final int COST = 3;

	public RepeatForm() {
        super(ID, NAME, COST, DESCRIPTION, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
        this.isEthereal = true;
	}

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new RepeatFormAction(this));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.rawDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
            this.isEthereal = false;
        }
    }
}