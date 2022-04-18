
package testmod.cards.curse;

import testmod.cards.AbstractTestCurseCard;
import testmod.powers.GreedPower;
import testmod.relics.Sins;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;

public class Greed extends AbstractTestCurseCard {
    public static final String ID = "Greed";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int BASE_MGC = 1;

    public Greed() {
    	super(ID, NAME, DESCRIPTION);
    	this.magicNumber = this.baseMagicNumber = BASE_MGC;
    	this.exhaust = true;
    }

	public void triggerWhenDrawn() {
		this.addToBot(new ApplyPowerAction(p(), p(), new GreedPower(p(), this.magicNumber), this.magicNumber));
	}
    
    public AbstractCard makeCopy() {
		if (Sins.isObtained())
	        return new Greed();
		return Sins.copyCurse();
    }
}