
package cards.colorless;

import cards.AbstractUpdatableCard;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;

import java.util.stream.Stream;


public class BloodShelter extends AbstractUpdatableCard {
    public static final String ID = "BloodShelter";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    private static final int COST = 3;
    private static final int BLOCK = -1;
    private static final String DESCRIPTION = getDescription(BLOCK, false, false);

    private static String getDescription(int value, boolean onMonster, boolean hovered) {
		String tmp = EXTENDED_DESCRIPTION[0];
    	if (value > -1 && onMonster && hovered)
    		tmp += "(" + value + ")";
    	return tmp + EXTENDED_DESCRIPTION[1];
    }
    
    public BloodShelter() {
        super(ID, NAME, COST, DESCRIPTION, CardType.SKILL, CardRarity.RARE, CardTarget.ENEMY);
        this.baseBlock = 0;
        this.exhaust = true;
    }
    
    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.preApplyPowers(p, m);
    	super.applyPowers();
    	Stream.of(p, m).forEach(c -> this.addToBot(new GainBlockAction(c, p, this.block)));
        this.addTmpActionToBot(() -> {
        	double rate = Stream.of(p, m).mapToDouble(c -> c.currentHealth * 1.0 / c.maxHealth).sum();
        	Stream.of(p, m).forEach(c -> f(c, rate));
        });
    }
    
    private static void f(AbstractCreature c, double rate) {
		c.currentHealth = Math.max(1, (int) (rate / 2 * c.maxHealth));
    	c.healthBarUpdatedEvent();
	}
    
	@Override
	public void preApplyPowers(AbstractPlayer p, AbstractMonster m) {
		if (m == null) {
			this.baseBlock = 0;
			this.onMonster = false;
			return;
		}
		this.onMonster = true;
		double rate = p.currentHealth * 1.0 / p.maxHealth + m.currentHealth * 1.0 / m.maxHealth;
    	int prePHealth = p.currentHealth;
    	int preMHealth = m.currentHealth;
    	int postPHealth = (int) (rate / 2 * p.maxHealth);
    	if (postPHealth == 0)
    		postPHealth = 1;
    	int postMHealth = (int) (rate / 2 * m.maxHealth);
    	if (postMHealth == 0)
    		postMHealth = 1;
    	int amt = Math.abs(prePHealth - postPHealth) + Math.abs(preMHealth - postMHealth);
        this.baseBlock = amt;
	}
	
	public void applyPowers() {
		this.changeDescription(getDescription(this.baseBlock, this.onMonster, this.isHovered()), false);
    	super.applyPowers();
    }
	
    public AbstractCard makeCopy() {
    	BloodShelter c = new BloodShelter();
    	TO_UPDATE.add(c);
        return c;
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(2);
        }
    }

}