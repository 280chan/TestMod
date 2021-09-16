
package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.SubstituteBySubterfugeAction;

import com.megacrit.cardcrawl.localization.CardStrings;

public class SubstituteBySubterfuge extends AbstractTestCard {
	public static final String ID = "SubstituteBySubterfuge";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final String E = " [E] ";
	private static final int COST = 0;

	public SubstituteBySubterfuge() {
		super(ID, NAME, COST, getDescription(false), CardType.SKILL, CardRarity.UNCOMMON, CardTarget.NONE);
		this.exhaust = true;
	}

	public static String getDescription(boolean upgraded) {
		String temp = EXTENDED_DESCRIPTION[0] + E + EXTENDED_DESCRIPTION[1];
		if (!upgraded)
			temp += EXTENDED_DESCRIPTION[2];
		return temp;
	}
	
	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(new GainEnergyAction(1));
		this.addToBot(new SubstituteBySubterfugeAction(p));
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.rawDescription = getDescription(true);
			this.initializeDescription();
			this.exhaust = false;
		}
	}
}