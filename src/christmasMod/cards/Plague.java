package christmasMod.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.NoxiousFumesPower;

import christmasMod.powers.PlaguePower;

public class Plague extends AbstractChristmasCard {
    public static final String ID = "Plague";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int BASE_MGC = 1;
    private static final int POISON_AMT = 5;
    
    public Plague() {
        super(ID, NAME, COST, DESCRIPTION, CardType.POWER, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new ApplyPowerAction(p, p, new PlaguePower(p, this.magicNumber), this.magicNumber));
		this.addToBot(new ApplyPowerAction(p, p, new NoxiousFumesPower(p, POISON_AMT), POISON_AMT));
	}

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }
}