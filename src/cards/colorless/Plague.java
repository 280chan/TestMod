
package cards.colorless;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.localization.CardStrings;

import cards.AbstractTestCard;
import powers.PlagueActPower;
import powers.PlaguePower;

public class Plague extends AbstractTestCard {
    public static final String ID = "Plague";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    private static final int COST = -1;

    public Plague() {
        super(ID, NAME, COST, DESCRIPTION, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
        this.isEthereal = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addTmpXCostActionToBot(this, e -> {
			this.addToTop(new ApplyPowerAction(p, p, new PlaguePower(p, e), e));
    	});
    	this.addToBot(new ApplyPowerAction(p, p, new PlagueActPower(p)));
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.isEthereal = false;
        	this.rawDescription = UPGRADE_DESCRIPTION;
        	this.initializeDescription();
            this.upgradeName();
        }
    }
}