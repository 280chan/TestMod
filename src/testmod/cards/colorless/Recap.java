package testmod.cards.colorless;

import testmod.cards.AbstractTestCard;
import testmod.powers.RecapPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

public class Recap extends AbstractTestCard {

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.atb(apply(p, new RecapPower(p, this.magicNumber)));
	}
	
	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeBaseCost(1);
		}
	}
}