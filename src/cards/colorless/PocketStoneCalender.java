
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.ModifyCostForCombatAction;

import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.*;  //改成自己的import

public class PocketStoneCalender extends CustomCard {
    public static final String ID = "LackOfEnergy";
    public static final String NAME = "掌上历石";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    private static final String[] DESC = {"造成回合数的平方", "( !D! )", "点伤害。打出这张牌时在本场战斗耗能增加 !M! 。抽到这张牌时在本场战斗耗能降低1。"};
    public static final String DESCRIPTION = DESC[0] + DESC[2];
    private static final int COST = 0;//卡牌费用
    private static final int ATTACK_DMG = 0;//基础伤害值
    private static final int BASE_MGC = 2;

    public PocketStoneCalender() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseDamage = ATTACK_DMG;
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
    }

    public void triggerWhenDrawn() {
    	this.modifyCostForCombat(-1);
    	this.costForTurn = this.cost;
    }
    
    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
    	AbstractDungeon.actionManager.addToBottom(new ModifyCostForCombatAction(this.uuid, this.magicNumber));
    }

    public void applyPowers() {
    	this.rawDescription = DESC[0] + DESC[1] + DESC[2];
    	this.initializeDescription();
    	int turn = GameActionManager.turn;
    	this.baseDamage = turn * turn;
    	super.applyPowers();
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();//改名，其实就是多个+
            this.upgradeMagicNumber(-1);//升级增加的特殊常量MagicNumber
        }
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}