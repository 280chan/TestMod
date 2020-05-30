
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.ComboMasterAction;
import actions.ComboMasterUpgradeAction;

import com.megacrit.cardcrawl.dungeons.*;

public class ComboMaster extends CustomCard {
    public static final String ID = "ComboMaster";
    public static final String NAME = "连击专家";
    public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "对所有敌人造成 !D! 点伤害 !M! 次。每打出一次，本场战斗中基础伤害和攻击次数都增加1。";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
    private static final int COST = 2;//卡牌费用
    private static final int ATTACK_DMG = 1;//基础伤害值
    private static final int BASE_MGC = 1;
    
    public ComboMaster() {
    	super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.UNCOMMON, CardTarget.ALL_ENEMY);
    	this.baseDamage = ATTACK_DMG;
    	this.baseMagicNumber = BASE_MGC;
    	this.magicNumber = this.baseMagicNumber;
    	this.isMultiDamage = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	AbstractDungeon.actionManager.addToBottom(new ComboMasterAction(p, this.multiDamage, this.magicNumber, this.damageTypeForTurn));
    	AbstractDungeon.actionManager.addToBottom(new ComboMasterUpgradeAction(this.uuid));
    }
    
    public AbstractCard makeCopy() {
		return new ComboMaster();
    }

    public void upgradeValues() {
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