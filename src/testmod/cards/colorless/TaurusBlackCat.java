package testmod.cards.colorless;

import testmod.cards.AbstractTestCard;
import testmod.powers.TaurusBlackCatPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

public class TaurusBlackCat extends AbstractTestCard {
	private static final int BASE_MGC = 80;

	public TaurusBlackCat() {
		super(3, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
		this.magicNumber = this.baseMagicNumber = BASE_MGC;
	}
	
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