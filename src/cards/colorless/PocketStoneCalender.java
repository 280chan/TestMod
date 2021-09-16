
package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.ModifyCostForCombatAction;

import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.*;

public class PocketStoneCalender extends AbstractTestCard {
    public static final String ID = "PocketStoneCalender";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    private static final String DESCRIPTION = EXTENDED_DESCRIPTION[0] + EXTENDED_DESCRIPTION[2];
    private static final int COST = 0;
    private static final int ATTACK_DMG = 0;
    private static final int BASE_MGC = 2;

    public PocketStoneCalender() {
        super(ID, NAME, COST, DESCRIPTION, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseDamage = ATTACK_DMG;
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void triggerWhenDrawn() {
    	this.modifyCostForCombat(-1);
    	this.costForTurn = this.cost;
    }
    
    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
    	this.addToBot(new ModifyCostForCombatAction(this.uuid, this.magicNumber));
    }
    
    public void calculateCardDamage(AbstractMonster m) {
    	int turn = GameActionManager.turn;
    	this.baseDamage = turn * turn;
    	super.calculateCardDamage(m);
    	this.rawDescription = EXTENDED_DESCRIPTION[0] + EXTENDED_DESCRIPTION[1] + EXTENDED_DESCRIPTION[2];
    	this.initializeDescription();
    }

    public void applyPowers() {
    	int turn = GameActionManager.turn;
    	this.baseDamage = turn * turn;
    	super.applyPowers();
    	this.rawDescription = EXTENDED_DESCRIPTION[0] + EXTENDED_DESCRIPTION[1] + EXTENDED_DESCRIPTION[2];
    	this.initializeDescription();
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(-1);
        }
    }
}