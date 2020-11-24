package christmasMod.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;

import christmasMod.powers.GiftInfinitePower;

public class GiftInfinite extends AbstractChristmasCard {
    public static final String ID = "GiftInfinite";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final String UPGRADED_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    private static final int COST = 1;
    private static final int BASE_MGC = 1;
    
    public GiftInfinite() {
        super(ID, NAME, COST, DESCRIPTION, CardType.POWER, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(new ApplyPowerAction(p, p, new GiftInfinitePower(p, this.magicNumber, this.upgraded), this.magicNumber));
	}

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
        }
    }
}