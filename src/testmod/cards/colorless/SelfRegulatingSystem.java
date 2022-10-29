package testmod.cards.colorless;

import testmod.cards.AbstractTestCard;
import testmod.powers.SelfRegulatingSystemPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

public class SelfRegulatingSystem extends AbstractTestCard {

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.atb(apply(p, new SelfRegulatingSystemPower(p, this.magicNumber)));
	}
	
	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.isInnate = true;
			this.upDesc();
		}
	}
}