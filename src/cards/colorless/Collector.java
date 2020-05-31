
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.*;

import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;

public class Collector extends CustomCard {
    public static final String ID = "Collector";
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(TestMod.makeID(ID));
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG = TestMod.cardIMGPath("relic1");
    private static final int COST = 2;
    private static final int ATTACK_DMG = 8;
    private static final int BASE_MGC = 1;
    
    public Collector() {
    	super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.RARE, CardTarget.ALL_ENEMY);
    	this.baseDamage = ATTACK_DMG;
    	this.misc = this.baseDamage;
    	this.baseMagicNumber = BASE_MGC;
    	this.magicNumber = this.baseMagicNumber;
    	this.isMultiDamage = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	AttackEffect e = AttackEffect.SLASH_DIAGONAL;
    	if (this.damage > 50)
	        e = AttackEffect.SLASH_HEAVY;
	    else if (this.damage > 30)
	        e = AttackEffect.SLASH_HORIZONTAL;
    	if (this.multiDamage == null)
    		this.applyPowers();
    	AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, e));
    }

    public void applyPowers() {
    	this.baseDamage = this.misc + this.magicNumber * this.getRelics();
    	super.applyPowers();
    }
    
    private int getRelics() {
    	return AbstractDungeon.player.relics.size();
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();//改名，其实就是多个+
            this.upgradeMagicNumber(1);
        }
    }
}