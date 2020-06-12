package cards;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import utils.MiscMethods;

public abstract class AbstractTestCurseCard extends AbstractTestCard implements MiscMethods {
	
	public AbstractTestCurseCard(String shortID, String NAME, String DESCRIPTION) {
        super(shortID, NAME, DESCRIPTION);
    }
	
	public boolean canUse(AbstractPlayer p, AbstractMonster m) {
		return this.hasPrudence() || p.hasRelic("Blue Candle");
	}
	
	public void use(final AbstractPlayer p, final AbstractMonster m) {
	}
	
	public void upgrade() {
	}
	
}
