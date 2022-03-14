
package testmod.cards.curse;

import testmod.cards.AbstractTestCurseCard;
import testmod.relics.Sins;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;

public class Gluttony extends AbstractTestCurseCard {
    public static final String ID = "Gluttony";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int BASE_MGC = 20;

    public Gluttony() {
    	super(ID, NAME, DESCRIPTION);
    	this.magicNumber = this.baseMagicNumber = BASE_MGC;
    	this.exhaust = true;
    }
    
    public void onRemoveFromMasterDeck() {
		AbstractDungeon.player.decreaseMaxHealth(AbstractDungeon.player.maxHealth * this.magicNumber / 100);
    }
    
    public AbstractCard makeCopy() {
		if (Sins.isObtained())
	        return new Gluttony();
		return Sins.copyCurse();
    }
}