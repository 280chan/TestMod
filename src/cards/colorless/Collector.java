
package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;

public class Collector extends AbstractTestCard {
    public static final String ID = "Collector";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 2;
    private static final int ATTACK_DMG = 8;
    private static final int BASE_MGC = 1;
    
    public Collector() {
    	super(ID, NAME, COST, DESCRIPTION, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ALL_ENEMY);
    	this.baseDamage = ATTACK_DMG;
    	this.misc = this.baseDamage;
    	this.baseMagicNumber = BASE_MGC;
    	this.magicNumber = this.baseMagicNumber;
    	this.isMultiDamage = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	AttackEffect e = AttackEffect.SLASH_DIAGONAL;
    	if (this.damage > 40)
	        e = AttackEffect.SLASH_HEAVY;
	    else if (this.damage > 20)
	        e = AttackEffect.SLASH_HORIZONTAL;
    	if (this.multiDamage == null)
    		this.applyPowers();
    	this.addToBot(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, e));
    }

    public void calculateCardDamage(AbstractMonster m) {
    	this.baseDamage = this.misc + this.magicNumber * this.getRelics();
    	super.calculateCardDamage(m);
    }
    
    private int getRelics() {
    	return AbstractDungeon.player.relics.size();
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }
}