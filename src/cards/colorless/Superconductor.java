
package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.SuperconductorAction;

import com.megacrit.cardcrawl.localization.CardStrings;

public class Superconductor extends AbstractTestCard {
    public static final String ID = "Superconductor";
	private static final CardStrings cardStrings = AbstractTestCard.Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final String UPGRADED_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    private static final int COST = -1;

    public Superconductor() {
        super(ID, NAME, COST, DESCRIPTION, CardType.SKILL, CardRarity.RARE, CardTarget.NONE);
        this.exhaust = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        this.addToBot(new SuperconductorAction(p, this.freeToPlayOnce, this.energyOnUse));
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