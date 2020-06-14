
package cards.colorless;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.PowerStrikeAction;
import cards.AbstractTestCard;

import com.megacrit.cardcrawl.localization.CardStrings;

public class PowerStrike extends AbstractTestCard {
    public static final String ID = "PowerStrike";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    private static final int COST = 3;
    private static final int BASE_MGC = 5;
    private static final int BASE_PHASE = 1;

    public PowerStrike() {
        super(ID, NAME, COST, getDescription(BASE_MGC), CardType.SKILL, CardRarity.RARE, CardTarget.ALL_ENEMY);
        this.exhaust = true;
        this.isEthereal = true;
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
        this.tags.add(CardTags.STRIKE);
    }
    
    public static String getDescription(int mgc) {
    	if (mgc % 10 == 0) {
    		return EXTENDED_DESCRIPTION[0] + (BASE_PHASE + mgc / 10) + EXTENDED_DESCRIPTION[1];
    	} else if (mgc < 10) {
    		return EXTENDED_DESCRIPTION[0] + BASE_PHASE + ". !M! " + EXTENDED_DESCRIPTION[1];
    	}
		return EXTENDED_DESCRIPTION[0] + (BASE_PHASE + mgc / 10) + "." + (mgc % 10) + EXTENDED_DESCRIPTION[1];
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new PowerStrikeAction(p, this.magicNumber));
    }
    
    public boolean canUpgrade() {
    	return true;
    }
    
    public void upgrade() {
    	if (!this.upgraded)
    		this.upgraded = true;
    	this.name = NAME + "+" + ++this.timesUpgraded;
        this.initializeTitle();
        this.upgradeMagicNumber(1);
        this.rawDescription = getDescription(this.magicNumber);
        this.initializeDescription();
    }
}