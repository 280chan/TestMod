package cards.colorless;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import actions.ReproduceAction;
import cards.AbstractTestCard;

public class Reproduce extends AbstractTestCard {
    private static final int BASE_MGC = 1;

    public Reproduce() {
        super(Reproduce.class, 1, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
        this.isInnate = true;
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new ReproduceAction(this, this.magicNumber));
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(0);
        }
    }
}