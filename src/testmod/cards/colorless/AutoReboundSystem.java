
package testmod.cards.colorless;

import testmod.cards.AbstractTestCard;
import testmod.powers.AutoReboundPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.BerserkPower;
import com.megacrit.cardcrawl.powers.DrawPower;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;

public class AutoReboundSystem extends AbstractTestCard {
	public static final String ID = "AutoReboundSystem";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final int COST = 3;
	private static final int BASEMAGIC = 1;

	public AutoReboundSystem() {
		super(ID, NAME, COST, getDescription(), CardType.POWER, CardRarity.RARE, CardTarget.SELF);
		this.magicNumber = this.baseMagicNumber = BASEMAGIC;
	}

	public static String getDescription() {
		return getDescription(BASEMAGIC);
	}
	
	public static String getDescription(int magic) {
		String temp = EXTENDED_DESCRIPTION[0];
		String e = " [E]";
		if (magic < 4 && magic > 0) {
			for (int i = 0; i < magic; i++)
				temp += e;
		} else
			temp += magic + e;
		temp += EXTENDED_DESCRIPTION[1];
		return temp;
	}
	
	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(new ApplyPowerAction(p, p, new AutoReboundPower(p, this.magicNumber), this.magicNumber));
		this.addToBot(new ApplyPowerAction(p, p, new BerserkPower(p, this.magicNumber), this.magicNumber));
		this.addToBot(new ApplyPowerAction(p, p, new DrawPower(p, this.magicNumber), this.magicNumber));
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(1);
			this.rawDescription = getDescription(magicNumber);
			this.initializeDescription();
		}
	}
}