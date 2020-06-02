
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.*;

import actions.FibonacciUpgradeAction;

import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

public class RabbitOfFibonacci extends CustomCard {
    public static final String ID = "RabbitOfFibonacci";
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(TestMod.makeID(ID));
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String IMG = TestMod.cardIMGPath("relic1");
    private static final int COST = 2;
    private static final int BASE_DMG = 1;
    private static final int BASE_BLK = 1;

    public RabbitOfFibonacci() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.UNCOMMON, CardTarget.ALL);
        this.baseDamage = BASE_DMG;
        this.baseBlock = BASE_BLK;
        this.misc = BASE_DMG;
        this.isMultiDamage = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new GainBlockAction(p, p, this.block));
    	this.addToBot(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AttackEffect.SLASH_HORIZONTAL));
    	this.addToBot(new FibonacciUpgradeAction(this.uuid));
    }

    public boolean canUpgrade() {
    	return true;
    }
    
    public void upgrade() {
    	this.timesUpgraded += 1;
    	this.name = NAME + "+" + this.timesUpgraded;
    	this.upgraded = true;
        this.initializeTitle();
        int tmp = this.baseDamage;
        this.upgradeDamage(this.misc);
        this.upgradeBlock(this.misc);
        this.misc = tmp;
    }
}