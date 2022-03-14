
package testmod.cards.curse;

import testmod.cards.AbstractTestCurseCard;
import testmod.relics.Sins;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.FocusPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;

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
		AbstractPlayer p = AbstractDungeon.player;
	    this.addToBot(new ApplyPowerAction(p, p, new FocusPower(p, -this.magicNumber), -this.magicNumber));
	    this.addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, -this.magicNumber), -this.magicNumber));
	    this.addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, -this.magicNumber), -this.magicNumber));
	    this.addToBot(new ApplyPowerAction(p, p, new WeakPower(p, 3, false), 3));
	    this.addToBot(new ApplyPowerAction(p, p, new FrailPower(p, 3, false), 3));
	    this.addToBot(new ApplyPowerAction(p, p, new VulnerablePower(p, 3, false), 3));
	}
    
    public AbstractCard makeCopy() {
		if (Sins.isObtained())
	        return new Lust();
		return Sins.copyCurse();
    }
}