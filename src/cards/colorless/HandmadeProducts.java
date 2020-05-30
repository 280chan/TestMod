
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import actions.HandmadeProductsAttackAction;

import com.megacrit.cardcrawl.dungeons.*;

public class HandmadeProducts extends CustomCard {
    public static final String ID = "HandmadeProducts";
    public static final String NAME = "手工产品";
    public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "造成你手牌中所有牌的耗能之积( !D! )点伤害。杀死敌人则耗能在本局游戏中增加 !M! 。能被多次 升级 。";
    private static final int COST = 0;
    private static final int BASE_DMG = 0;
    private static final int BASE_MGC = 1;

    public HandmadeProducts() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseDamage = BASE_DMG;
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new HandmadeProductsAttackAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), this.magicNumber, uuid));
    }
	
    public void calculateCardDamage(AbstractMonster m) {
		this.baseDamage = this.product();
		super.calculateCardDamage(m);
    }
    
	public void applyPowers() {
		this.baseDamage = this.product();
		super.applyPowers();
	}
	
	public int product() {
		int base = 1;
		for (AbstractCard c : AbstractDungeon.player.hand.group)
			if (c.cost > -1)
				base *= c.costForTurn;
			else if (c.cost == -1)
				base *= EnergyPanel.totalCount;
		if (!AbstractDungeon.player.hand.contains(this))
			base *= this.costForTurn;
		return base;
	}
    
    public boolean canUpgrade() {
    	return true;
    }
    
    public void upgrade() {
    	this.timesUpgraded++;
    	this.name = NAME + "+" + this.timesUpgraded;
    	this.initializeTitle();
        this.upgradeBaseCost(1);
    }
}