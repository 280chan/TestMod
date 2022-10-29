package testmod.cards.colorless;

import testmod.cards.AbstractTestCard;
import testmod.powers.ReverberationPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

public class Reverberation extends AbstractTestCard {

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.atb(apply(p, new ReverberationPower(p, this.magicNumber)));
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(1);
			this.isEthereal = false;
			this.upDesc();
		}
	}
}