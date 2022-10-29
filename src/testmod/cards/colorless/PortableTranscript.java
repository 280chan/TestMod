
package testmod.cards.colorless;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import testmod.cards.AbstractTestCard;
import testmod.powers.PortableTranscriptPower;

public class PortableTranscript extends AbstractTestCard {
	
	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.atb(this.apply(p, new PortableTranscriptPower(this.magicNumber)));;
	}
	
	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(1);
		}
	}
}