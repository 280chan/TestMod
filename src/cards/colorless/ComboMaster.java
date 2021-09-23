
package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;

import actions.ComboMasterAction;

public class ComboMaster extends AbstractTestCard {
    public static final String ID = "ComboMaster";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 2;
    private static final int ATTACK_DMG = 1;
    private static final int BASE_MGC = 1;
    
    public ComboMaster() {
    	super(ID, NAME, COST, DESCRIPTION, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ALL_ENEMY);
    	this.baseDamage = ATTACK_DMG;
    	this.magicNumber = this.baseMagicNumber = BASE_MGC;
    	this.isMultiDamage = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new ComboMasterAction(p, this.multiDamage, this.magicNumber, this.damageTypeForTurn));
    	this.addTmpActionToBot(this::changeValues);
    }
    
	private void changeValues() {
		GetAllInBattleInstances.get(this.uuid).stream().map(c -> (ComboMaster) c).forEach(ComboMaster::upgradeValues);
    }

    private void upgradeValues() {
        this.upgradeDamage(1);
        this.upgradeMagicNumber(1);
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeValues();
        }
    }
}