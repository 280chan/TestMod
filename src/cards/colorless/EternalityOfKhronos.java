
package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;

public class EternalityOfKhronos extends AbstractTestCard {
    public static final String ID = "EternalityOfKhronos";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final String UPGRADED_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    private static final int COST = 2;

    public EternalityOfKhronos() {
        super(ID, NAME, COST, DESCRIPTION, CardType.SKILL, CardRarity.RARE, CardTarget.NONE);
        this.isEthereal = this.exhaust = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.turnSkipperStartByCard(this);
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.exhaust = false;
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
        }
    }
}