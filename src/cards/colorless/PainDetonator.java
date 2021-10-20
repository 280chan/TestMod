package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

public class PainDetonator extends AbstractTestCard {
    private static final int BASE_MGC = 30;
    private static final int BASE_DMG = 0;
    
    public PainDetonator() {
        super(PainDetonator.class, 1, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseDamage = BASE_DMG;
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(
				new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AttackEffect.SLASH_HEAVY));
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