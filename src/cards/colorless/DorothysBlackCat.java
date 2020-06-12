
package cards.colorless;

import cards.AbstractTestCard;
import powers.DorothysBlackCatPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;

public class DorothysBlackCat extends AbstractTestCard {
    public static final String ID = "DorothysBlackCat";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 3;
    private static final int BASE_MGC = 75;

    public DorothysBlackCat() {
        super(ID, NAME, COST, DESCRIPTION, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }
    
    public void use(final AbstractPlayer p, final AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p, new DorothysBlackCatPower(p, this.magicNumber), this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(25);
        }
    }
}