package testmod.cards.colorless;

import testmod.actions.RepeatFormAction;
import testmod.cards.AbstractTestCard;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import basemod.helpers.BaseModCardTags;

public class RepeatForm extends AbstractTestCard {
	private static final int BASE_MGC = 1;

	public RepeatForm() {
        super(3, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
        this.isEthereal = true;
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
        this.tags.add(BaseModCardTags.FORM);
	}

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new RepeatFormAction(this));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upDesc();
            this.isEthereal = false;
        }
    }
}