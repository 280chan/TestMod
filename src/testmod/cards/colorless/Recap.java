package testmod.cards.colorless;

import testmod.cards.AbstractTestCard;
import testmod.powers.RecapPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

public class Recap extends AbstractTestCard {
    private static final int BASE_MGC = 1;

    public Recap() {
        super(2, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        this.addToBot(apply(p, new RecapPower(p, this.magicNumber)));
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(1);
        }
    }
}