package testmod.cards.colorless;

import testmod.cards.AbstractTestCard;
import testmod.powers.ShutDownPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.cards.blue.Reboot;

public class ShutDown extends AbstractTestCard {

	public ShutDown() {
		this.cardsToPreview = new Reboot();
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.atb(this.apply(p, new ShutDownPower(p, this.upgraded, this.magicNumber)));
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.cardsToPreview.upgrade();
			this.upDesc();
		}
	}
}