package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

public class EternalityOfKhronos extends AbstractTestCard {

    public EternalityOfKhronos() {
        super(EternalityOfKhronos.class, 2, CardType.SKILL, CardRarity.RARE, CardTarget.NONE);
        this.isEthereal = this.exhaust = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.turnSkipperStartByCard(this);
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.exhaust = false;
            this.rawDescription = this.upgradedDesc();
            this.initializeDescription();
        }
    }
}