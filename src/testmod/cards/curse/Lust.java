
package testmod.cards.curse;

import testmod.cards.AbstractTestCurseCard;
import testmod.relics.Sins;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.localization.CardStrings;

public class Lust extends AbstractTestCurseCard {
	public static final String ID = "Lust";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final int BASE_MGC = 1;

	public Lust() {
		super(ID, NAME, DESCRIPTION);
		this.magicNumber = this.baseMagicNumber = BASE_MGC;
	}

	public void triggerOnEndOfPlayerTurn() {
		this.atb(this.apply(p(), new WeakPower(p(), this.magicNumber, false)));
		this.atb(this.apply(p(), new FrailPower(p(), this.magicNumber, false)));
		this.atb(this.apply(p(), new VulnerablePower(p(), this.magicNumber, false)));
	}
	
	public AbstractCard makeCopy() {
		if (Sins.isObtained())
			return new Lust();
		return Sins.copyCurse();
	}
}