
package cards.colorless;

import cards.AbstractTestCard;
import powers.BloodBladePower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;

public class BloodBlade extends AbstractTestCard {
    public static final String ID = "BloodBlade";
	private static final CardStrings cardStrings = AbstractTestCard.Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final String UPGRADED_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    private static final int COST = 1;

    public BloodBlade() {
        super(ID, NAME, COST, DESCRIPTION, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
    }
    
    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	if (BloodBladePower.hasThis(this.upgraded)) {
    		BloodBladePower.getThis(this.upgraded).onFirstGain();
    	} else {
    		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new BloodBladePower(p, this.upgraded)));
    	}
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
        }
    }
}