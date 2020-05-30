
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.FibonacciUpgradeAction;

import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

public class RabbitOfFibonacci extends CustomCard {
    public static final String ID = "RabbitOfFibonacci";
    public static final String NAME = "斐波那契的兔子";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "获得 !B! 点格挡。对所有敌人造成 !D! 点伤害。将这张牌在本场战斗中 升级 一次。能被多次 升级 。";
    private static final int COST = 2;//卡牌费用
    private static final int BASE_DMG = 1;//基础伤害值
    private static final int BASE_BLK = 1;

    public RabbitOfFibonacci() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.UNCOMMON, CardTarget.ALL);
        this.baseDamage = BASE_DMG;
        this.baseBlock = BASE_BLK;
        this.misc = BASE_DMG;
        this.isMultiDamage = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
    	AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AttackEffect.SLASH_HORIZONTAL));
    	AbstractDungeon.actionManager.addToBottom(new FibonacciUpgradeAction(this.uuid));
    }
    
    public AbstractCard makeCopy() {
        return new RabbitOfFibonacci();
    }//复制卡牌后复制的卡，如果卡组里有复制卡牌的卡每张卡都要有这个

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