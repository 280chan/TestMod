package testmod.cards.colorless;

import testmod.actions.RepeatFormAction;
import testmod.cards.AbstractTestCard;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import basemod.helpers.BaseModCardTags;

public class RepeatForm extends AbstractTestCard {

	public RepeatForm() {
		this.tags.add(BaseModCardTags.FORM);
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.atb(new RepeatFormAction(this));
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upDesc();
			this.isEthereal = false;
		}
	}
}