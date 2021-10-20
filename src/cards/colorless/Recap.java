package cards.colorless;

import cards.AbstractTestCard;
import powers.RecapPower;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.actions.common.*;

public class Recap extends AbstractTestCard {
    private static final int BASE_MGC = 1;

    public Recap() {
        super(Recap.class, 2, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p, new RecapPower(p, this.magicNumber), this.magicNumber));
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(1);
        }
    }
}