package testmod.cards.colorless;

import testmod.cards.AbstractTestCard;
import testmod.powers.ReflectPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

public class Reflect extends AbstractTestCard {
    private static final int BASE_MGC = 1;

    public Reflect() {
        super(1, CardType.SKILL, CardRarity.RARE, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        this.addToBot(apply(p, new ReflectPower(p, this.magicNumber)));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }
}