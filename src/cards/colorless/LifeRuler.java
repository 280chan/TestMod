
package cards.colorless;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;

import cards.AbstractTestCard;

public class LifeRuler extends AbstractTestCard {
	public static final String ID = "LifeRuler";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final int COST = 1;
	private static final String DESCRIPTION = EXTENDED_DESCRIPTION[0] + EXTENDED_DESCRIPTION[1] + EXTENDED_DESCRIPTION[2];
	
	public LifeRuler() {
        super(ID, NAME, COST, DESCRIPTION, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.exhaust = true;
    }

	public void use(final AbstractPlayer p, final AbstractMonster t) {
		AbstractDungeon.getMonsters().monsters.stream().filter(not(m -> m.isDead || m.isDying || m.halfDead))
				.map(split(t(), m -> new ConstrictedPower(m, p, this.misc)))
				.forEach(consumer((m, cp) -> this.addToBot(new ApplyPowerAction(m, p, cp, this.misc))));
	}

	@Override
	public void calculateCardDamage(AbstractMonster m) {
		super.calculateCardDamage(m);
		AbstractPlayer p = AbstractDungeon.player;
    	this.misc = m == null ? 0 :Math.max(gcd(p.maxHealth, m.maxHealth), gcd(p.currentHealth, m.currentHealth));
    	this.rawDescription = this.getDesc();
		this.initializeDescription();
	}
	
	private static int gcd(int a, int b) {
		return (a == 0) ? b : gcd(b % a, a);
    }
    
    private String getDesc() {
		String tmp = EXTENDED_DESCRIPTION[0];
    	if (this.misc > 0) {
    		tmp += "(" + this.misc + ")";
    	}
    	tmp += EXTENDED_DESCRIPTION[1];
    	if (!this.upgraded) {
    		tmp += EXTENDED_DESCRIPTION[2];
    	}
    	return tmp;
	}
    
    public void resetAttributes() {
    	this.rawDescription = EXTENDED_DESCRIPTION[0] + EXTENDED_DESCRIPTION[1];
    	if (!this.upgraded) {
    		this.rawDescription += EXTENDED_DESCRIPTION[2];
    	}
		this.initializeDescription();
		super.resetAttributes();
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.exhaust = false;
            this.rawDescription = EXTENDED_DESCRIPTION[0] + EXTENDED_DESCRIPTION[1];
    		this.initializeDescription();
        }
    }

}