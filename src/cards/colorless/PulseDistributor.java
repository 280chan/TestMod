
package cards.colorless;

import cards.AbstractTestCard;
import powers.PulseDistributorPower;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;

public class PulseDistributor extends AbstractTestCard {
	public static final String ID = "PulseDistributor";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final int COST = 3;
	private static final int INITIAL_MAGIC = 1;

	public PulseDistributor() {
		super(ID, NAME, COST, DESCRIPTION, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
		this.magicNumber = this.baseMagicNumber = INITIAL_MAGIC;
		this.updatedDescription();
	}

	private void updatedDescription() {
		String temp = EXTENDED_DESCRIPTION[0];
		if (this.magicNumber != 0) {
			if (this.magicNumber > 0) {
				temp += "+";
			}
			temp += this.magicNumber;
		}
		this.rawDescription = temp + EXTENDED_DESCRIPTION[1];
		this.initializeDescription();
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		if (PulseDistributorPower.hasThis(p)) {
			PulseDistributorPower po = PulseDistributorPower.getThis(p);
			if (po.magic > this.magicNumber) {
				this.addToBot(new RemoveSpecificPowerAction(p, p, po));
				this.addToBot(new ApplyPowerAction(p, p, new PulseDistributorPower(p, this.magicNumber, po.DAMAGES)));
			}
		} else {
			this.addToBot(new ApplyPowerAction(p, p, new PulseDistributorPower(p, this.magicNumber)));
		}
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(-1);
			this.updatedDescription();
		}
	}
}