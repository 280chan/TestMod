package cards.colorless;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import actions.PowerStrikeAction;
import cards.AbstractTestCard;

public class PowerStrike extends AbstractTestCard {
    private static final int BASE_MGC = 5;

    public PowerStrike() {
        super(3, CardType.SKILL, CardRarity.RARE, CardTarget.ALL_ENEMY);
        this.exhaust = this.isEthereal = true;
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
        this.tags.add(CardTags.STRIKE);
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new PowerStrikeAction(p, this.magicNumber));
    }
    
    public boolean canUpgrade() {
    	return true;
    }
    
    public void upgrade() {
    	this.upgraded = true;
    	this.name = this.name() + "+" + ++this.timesUpgraded;
        this.initializeTitle();
        this.upgradeMagicNumber(1);
		this.upDesc(exDesc()[0]
				+ (magicNumber % 10 == 0 ? (1 + magicNumber / 10)
						: (magicNumber < 10 ? 1 + ". !M! " : (1 + magicNumber / 10) + "." + (magicNumber % 10)))
				+ exDesc()[1]);
    }
}