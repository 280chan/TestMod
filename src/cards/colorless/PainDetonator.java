
package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

public class PainDetonator extends AbstractTestCard {
    public static final String ID = "PainDetonator";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int BASE_MGC = 30;
    private static final int BASE_DMG = 0;
    
    public PainDetonator() {
        super(ID, NAME, COST, DESCRIPTION, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseDamage = BASE_DMG;
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AttackEffect.SLASH_HEAVY));
    }
    
    public void calculateCardDamage(AbstractMonster m) {
    	this.baseDamage = (int) ((m.maxHealth - m.currentHealth) * this.magicNumber / 100f);
    	super.calculateCardDamage(m);
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(20);
        }
    }
}