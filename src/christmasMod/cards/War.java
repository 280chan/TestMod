package christmasMod.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;

import christmasMod.powers.WarPower;

public class War extends AbstractChristmasCard {
    public static final String ID = "War";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 2;
    private static final int BASE_MGC = 1;
    
    public War() {
        super(ID, NAME, COST, DESCRIPTION, CardType.POWER, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new ApplyPowerAction(p, p, new WarPower(p, this.magicNumber), this.magicNumber));
	}

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(1);
        }
    }
}