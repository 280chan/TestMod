package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.*;

public class PocketStoneCalender extends AbstractTestCard {
    private static final int BASE_DMG = 0;
    private static final int BASE_MGC = 2;

    public PocketStoneCalender() {
        super(PocketStoneCalender.class, 0, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseDamage = BASE_DMG;
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void triggerWhenDrawn() {
    	this.modifyCostForCombat(-1);
    	this.costForTurn = this.cost;
    }
    
	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
				AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
		this.addTmpActionToBot(() -> GetAllInBattleInstances.get(this.uuid).forEach(this::modifyCostForCombat));
	}
    
    public void modifyCostForCombat(AbstractCard c) {
    	c.modifyCostForCombat(this.magicNumber);
    }
    
    private void putDamage(int turn) {
    	this.baseDamage = turn * turn;
    }
    
    public void calculateCardDamage(AbstractMonster m) {
    	this.putDamage(GameActionManager.turn);
    	super.calculateCardDamage(m);
    	this.upDesc();
    }

    public void applyPowers() {
    	this.putDamage(GameActionManager.turn);
    	super.applyPowers();
    	this.upDesc();
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(-1);
        }
    }
}