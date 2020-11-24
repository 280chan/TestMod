package christmasMod.cards;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;

import christmasMod.actions.GiftMagicAction;

public class GiftMagic extends AbstractChristmasCard {
    public static final String ID = "GiftMagic";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final String UPGRADED_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    private static final int COST = 1;
    
    public GiftMagic() {
        super(ID, NAME, COST, DESCRIPTION, CardType.SKILL, CardTarget.NONE);
        this.isEthereal = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(new GiftMagicAction());
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
            this.isEthereal = false;
        }
    }
}