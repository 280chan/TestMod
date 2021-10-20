package cards.colorless;

import cards.AbstractTestCard;
import powers.ShutDownPower;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.cards.blue.Reboot;

public class ShutDown extends AbstractTestCard {
    private static final int BASE_MGC = 1;

    public ShutDown() {
        super(ShutDown.class, 1, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
        this.cardsToPreview = new Reboot();
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        this.addToBot(this.apply(p, new ShutDownPower(p, this.upgraded, this.magicNumber)));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.cardsToPreview.upgrade();
            this.upDesc();
        }
    }
}