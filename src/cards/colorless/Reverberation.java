package cards.colorless;

import cards.AbstractTestCard;
import powers.ReverberationPower;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.actions.common.*;

public class Reverberation extends AbstractTestCard {
    private static final int BASE_MGC = 1;

    public Reverberation() {
        super(Reverberation.class, 3, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
        this.isEthereal = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p, new ReverberationPower(p, this.magicNumber), this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
            this.isEthereal = false;
            this.upDesc();
        }
    }
}