package testmod.cards.colorless;

import testmod.cards.AbstractTestCard;
import testmod.powers.SelfRegulatingSystemPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

public class SelfRegulatingSystem extends AbstractTestCard {
	private static final int BASE_MGC = 1;

	public SelfRegulatingSystem() {
		super(2, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
		this.magicNumber = this.baseMagicNumber = BASE_MGC;
	}

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