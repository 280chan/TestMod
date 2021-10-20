package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import actions.RepeatFormAction;

public class RepeatForm extends AbstractTestCard {
	private static final int BASE_MGC = 1;

	public RepeatForm() {
        super(RepeatForm.class, 3, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
        this.isEthereal = true;
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
	}

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new RepeatFormAction(p, this));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upDesc();
            this.isEthereal = false;
        }
    }
}