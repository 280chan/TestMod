package christmasMod.cards;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;

import christmasMod.actions.GiftDisturbAction;

public class GiftDisturb extends AbstractChristmasCard {
    public static final String ID = "GiftDisturb";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final String UPGRADED_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    private static final int COST = -1;
    
    public GiftDisturb() {
        super(ID, NAME, COST, DESCRIPTION, CardType.SKILL, CardTarget.ENEMY);
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(new GiftDisturbAction(p, m, this.freeToPlayOnce, this.upgraded, this.energyOnUse));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
        }
    }
}