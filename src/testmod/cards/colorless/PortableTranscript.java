
package testmod.cards.colorless;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import testmod.cards.AbstractTestCard;
import testmod.powers.PortableTranscriptPower;

public class PortableTranscript extends AbstractTestCard {
	private static final int COST = 3;
	private static final int BASE_MGC = 1;

	public PortableTranscript() {
		super(COST, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
		this.magicNumber = this.baseMagicNumber = BASE_MGC;
		this.isInnate = true;
	}

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