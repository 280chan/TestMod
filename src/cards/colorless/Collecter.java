
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import com.megacrit.cardcrawl.dungeons.*;

public class Collecter extends CustomCard {
    public static final String ID = "Collecter";
    public static final String NAME = "收藏家";
    public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "对所有敌人造成 !D! 点伤害。你的每件遗物增加 !M! 点伤害。";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
    public static final String UPGRADED_DESCRIPTIONS = "对所有敌人造成 !D! 点伤害。你的每件遗物增加 !M! 点伤害。";
    private static final int COST = 2;//卡牌费用
    private static final int ATTACK_DMG = 8;//基础伤害值
    private static final int BASE_MGC = 1;
    
    public Collecter() {
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
    
    public AbstractCard makeCopy() {
		return new Collecter();
    }//复制卡牌后复制的卡，如果卡组里有复制卡牌的卡每张卡都要有这个

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
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}