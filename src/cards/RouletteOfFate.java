
package cards;

import basemod.abstracts.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.BlurPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.NoDrawPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.ThornsPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

import actions.rouletteOfFate.AbstractRouletteOfFateAction;

import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.ApplyBulletTimeAction;
import com.megacrit.cardcrawl.actions.unique.ArmamentsAction;
import com.megacrit.cardcrawl.actions.unique.DiscardPileToTopOfDeckAction;
import com.megacrit.cardcrawl.actions.unique.DualWieldAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.*;  //改成自己的import

/**
 * @deprecated
 */
public class RouletteOfFate extends CustomCard {
    public static final String ID = "RouletteOfFate";
    public static final String NAME = "命运轮盘";
    public static final String IMG = "resources/images/relic1.png";
    public static final String DESCRIPTION = "全能test";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
    private static final int COST = -2;//卡牌费用
    private static final int BASE_BLC = 0;//基础格挡值
    private static final int BASE_ATK = 0;//基础伤害值
    
    private ArrayList<AbstractRouletteOfFateAction> actions;

    public RouletteOfFate() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.CURSE, CardColor.CURSE, CardRarity.CURSE, CardTarget.NONE);
        this.baseBlock = 5;//基础格挡值. this.block为有敏捷等加成的格挡值.
        this.baseDamage = 6;//基础伤害值. this.damage为有力量、钢笔尖等加成的伤害值.
        this.baseMagicNumber = 1;//特殊值，一般用来叠BUFF层数。和下一行连用。
        this.magicNumber = this.baseMagicNumber;
        
        this.isEthereal = false;//虚无属性，false不虚无，true虚无。可在该类里调用改变。不虚无就可以赋值为false或者删掉这一行
        this.exhaust = false;//消耗属性，false不消耗，true消耗。可在该类里调用改变。不消耗就可以赋值为false或者删掉这一行
        this.isInnate = false;//固有属性，false不固有，true固有。可在该类里调用改变。不固有就可以赋值为false或者删掉这一行
        
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	//括号内变量的含义：              声明局部变量p为玩家    ,      声明局部变量m为敌人            .
    	
        p.gold -= 10;//减10金币
        this.baseDamage = p.maxHealth - p.currentHealth;//这张卡的伤害变成已损失血量的伤害
        this.baseBlock = p.maxHealth - p.currentHealth;//这张卡的格挡变成已损失血量的格挡

        AbstractDungeon.actionManager.addToBottom(new DualWieldAction(p, this.magicNumber));//将手牌中某张非技能卡复制magicNumber次
        AbstractDungeon.actionManager.addToBottom(new ExhaustAction(p, p, this.magicNumber, false));//消耗手中的magicNumber张指定牌（最后一个参数决定是否随机选牌 false不随机 true随机）
        AbstractDungeon.actionManager.addToTop(new DiscardAction(p, p, this.magicNumber, false));//丢弃手中的magicNumber张指定牌（最后一个参数决定是否随机选牌 false不随机 true随机）
        
        AbstractCard c = new RouletteOfFate();                                                          //创建一个新的卡牌对象c为卡Modbasecard,可以用c.调用卡Modbasecard里面的方法、参数。格式： AbstractCard c = new 卡牌类名();不局限于和下面一句联用。
        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(c, this.magicNumber));//将magicNumber张c卡复制到手牌中。和上面一句连用。c可更换为 new 卡牌类名();
        
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, this.magicNumber));//抽magicNumber张卡片,只在玩家回合生效.
        AbstractDungeon.actionManager.addToBottom(new ArmamentsAction(false));//本场战斗升级手中的一张牌
        AbstractDungeon.actionManager.addToBottom(new DiscardPileToTopOfDeckAction(p));//将弃牌库中的一张牌放到牌库顶端


        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new PlatedArmorPower(p, this.magicNumber), this.magicNumber));//为自身增加magicNumber层多层护甲
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new BlurPower(p, this.magicNumber), this.magicNumber));//为自身增加magicNumber层幻影
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new ThornsPower(p, this.magicNumber), this.magicNumber));//为自身增加magicNumber层荆棘
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new NoDrawPower(p)));//本回合内不能抽卡
        AbstractDungeon.actionManager.addToBottom(new ApplyBulletTimeAction());//本回合内所有卡牌消耗为0

        if (m == null || (m.intent != AbstractMonster.Intent.ATTACK && m.intent != AbstractMonster.Intent.ATTACK_BUFF && m.intent != AbstractMonster.Intent.ATTACK_DEBUFF && m.intent != AbstractMonster.Intent.ATTACK_DEFEND)) {
            //上面这一行的意思是，如果一名敌人这回合不是在攻击，则运行下面大括号内的语句(代码含义：条件 [对象m为空] 或 [对象m意图不为攻击 且 对象m意图不为攻击及强化且 对象m意图不为攻击及策略 且 对象m意图不为攻击及格挡] 中一个条件判定时返回true时执行大括号内的代码)
        	
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(2));//获得2点能量
            
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new DexterityPower(p, this.magicNumber), this.magicNumber));//获得magicNumber点敏捷
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber));//获得magicNumber点力量（把第一个p改成m，然后把magicNumber改成负数可以减敌人力量
            //ApplyPowerAction中的参数：(  p  , p  , new StrengthPower(p, this.magicNumber), this.magicNumber)  
            //参数含义：                被施加者,施加者,         能力种类   (被施加者， 施加层数)   ,计算层数   )一般施加层数和计算层数一致。
            
            AbstractDungeon.actionManager.addToBottom(new RemoveDebuffsAction(p));//解除自身所有异常
            AbstractDungeon.actionManager.addToBottom(new HealAction(p, p, this.magicNumber));//恢复magicNumber点生命
        }

        for (int i = 0; i < 3; ++i) {
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));//加甲
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));//造成伤害
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new WeakPower(m, this.magicNumber, false), this.magicNumber, true, AbstractGameAction.AttackEffect.NONE));//叠加虚弱magicNumber层
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new VulnerablePower(m, this.magicNumber, false), this.magicNumber, true, AbstractGameAction.AttackEffect.NONE));//叠加易伤magicNumber层
        }//多次效果，for循环内的语句将会被执行3次，次数可以自己改，就是i<后面那个值，里面的每行代码都可拿出for循环在use里使用。
    }
    
    public static int countCards() {//构造一个静态函数countCards。返回值类型为int型。
        int count = 0;//声明一个局部变量count初始量为0；
        for (final AbstractCard c : AbstractDungeon.player.hand.group) {
        	// 对玩家的每张手牌执行一次大括号内的代码。(AbstractDungeon.player.masterDeck.group 原始牌组（按D键打开的那个牌组）， AbstractDungeon.player.hand.group 手牌组， AbstractDungeon.player.AbstractDungeon.player.drawPile.group 抽牌堆组， AbstractDungeon.player.discardPile.group 弃牌堆组)
            if (c.type == AbstractCard.CardType.SKILL) {
            	//对手牌的类型进行判定，当类型为技能时返回true值，执行if大括号内的代码，类型不为技能时返回false不执行if大括号内代码结束。
                ++count;
                //变量count在手牌为技能牌时数值 +1；
            }
        }
        return count;//重复执行后，返回的count值为当前手牌里技能牌的数量。
    }//数手上有多少张技能牌（AbstractCard.CardType.SKILL，技能，AbstractCard.CardType.ATTACK，攻击，AbstractCard.CardType.POWER，能力）
    //在该类里面可以通过调用countCards();得到该函数返回的count值。
    //例：this.magicNumber = countCards(); ---countCards()函数对手牌上满足条件的卡进行计数，数值为count，返回一个count，此时countCards()即为count这个局部变量的值。然后将该值赋值给this.magicNumber。

    public AbstractCard makeCopy() {
        return new RouletteOfFate();
    }//复制卡牌后复制的卡，如果卡组里有复制卡牌的卡每张卡都要有这个

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();//改名，其实就是多个+
            this.upgradeBlock(3);//升级增加的护甲
            this.upgradeDamage(3);//升级增加的伤害
            this.upgradeMagicNumber(1);//升级增加的特殊常量MagicNumber
            this.upgradeBaseCost(1);//这个跟其他不一样，是升级后的消耗值
            this.isEthereal = false;//虚无取消
            this.exhaust = false;//消耗取消
            this.isInnate = true;//固有，可以放到上面初始化的地方
        }
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}