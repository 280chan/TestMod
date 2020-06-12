
package cards.colorless;

import cards.AbstractTestCard;
import powers.ChaoticCorePower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.defect.IncreaseMaxOrbAction;

public class ChaoticCore extends AbstractTestCard {
    public static final String ID = "ChaoticCore";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 3;
    private static final int BASE_MGC = 1;
    private static final int ORB_SLOT = 1;

    public ChaoticCore() {
        super(ID, NAME, COST, DESCRIPTION, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new IncreaseMaxOrbAction(ORB_SLOT));
    	this.addToBot(new ApplyPowerAction(p, p, new ChaoticCorePower(p, this.magicNumber), this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }
}