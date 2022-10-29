package testmod.cards.colorless;

import testmod.cards.AbstractTestCard;
import testmod.powers.TaurusBlackCatPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

public class TaurusBlackCat extends AbstractTestCard {
	
	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.atb(apply(p, new TaurusBlackCatPower(p, this.magicNumber)));
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(20);
		}
	}
}