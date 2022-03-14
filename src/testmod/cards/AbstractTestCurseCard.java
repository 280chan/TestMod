package testmod.cards;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import testmod.utils.MiscMethods;

public abstract class AbstractTestCurseCard extends AbstractTestCard implements MiscMethods {
	
	public AbstractTestCurseCard(String shortID, String NAME, String DESCRIPTION) {
        super(shortID, NAME, DESCRIPTION);
    }
	
	public boolean canUse(AbstractPlayer p, AbstractMonster m) {
		return true;
	}
	
	public void use(final AbstractPlayer p, final AbstractMonster m) {
	}
	
	public void upgrade() {
	}
	
}
