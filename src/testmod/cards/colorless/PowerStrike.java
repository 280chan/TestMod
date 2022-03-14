package testmod.cards.colorless;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import testmod.actions.PowerStrikeAction;
import testmod.cards.AbstractTestCard;

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
    
    public void initializeDescription() {
		if (this.magicNumber > 4) {
			int mod = magicNumber % 10, whole = 1 + magicNumber / 10;
			this.rawDescription = exDesc()[0]
					+ (mod == 0 ? whole : (magicNumber < 10 ? 1 + ". !M! " : whole + "." + mod)) + exDesc()[1];
		}
    	super.initializeDescription();
    }
    
    public boolean canUpgrade() {
    	return true;
    }
    
    public void upgrade() {
    	this.upgraded = true;
    	this.name = this.name() + "+" + ++this.timesUpgraded;
        this.initializeTitle();
        this.upgradeMagicNumber(1);
		this.initializeDescription();
    }
}