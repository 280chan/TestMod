
package cards;

import basemod.abstracts.*;
import mymod.TestMod;

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
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.ApplyBulletTimeAction;
import com.megacrit.cardcrawl.actions.unique.ArmamentsAction;
import com.megacrit.cardcrawl.actions.unique.DiscardPileToTopOfDeckAction;
import com.megacrit.cardcrawl.actions.unique.DualWieldAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.actions.*;  //改成自己的import

/**
 * @deprecated
 */
public class 示例卡牌 extends CustomCard {
    public static final String ID = "Modbasecard";
    public static final String NAME = "名称";
    public static final String IMG = TestMod.cardIMGPath(ID);
    public static final String DESCRIPTION = "全能test";
    private static final int COST = -2;
    private static final int BASE_BLK = 5;
    private static final int BASE_DMG = 6;
    private static final int BASE_MGC = 1;

    public 示例卡牌() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.RARE, CardTarget.NONE);
        this.baseBlock = BASE_BLK;
        this.baseDamage = BASE_DMG;
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	//括号内变量的含义：              声明局部变量p为玩家    ,      声明局部变量m为敌人            .
    	
        p.gold -= 10;//减10金币
        this.baseDamage = p.maxHealth - p.currentHealth;//这张卡的伤害变成已损失血量的伤害
        this.baseBlock = p.maxHealth - p.currentHealth;//这张卡的格挡变成已损失血量的格挡

        AbstractDungeon.actionManager.addToBottom(new DualWieldAction(p, this.magicNumber));//将手牌中某张非技能卡复制magicNumber次
        AbstractDungeon.actionManager.addToBottom(new ExhaustAction(p, p, this.magicNumber, false));//消耗手中的magicNumber张指定牌（最后一个参数决定是否随机选牌 false不随机 true随机）
        AbstractDungeon.actionManager.addToTop(new DiscardAction(p, p, this.magicNumber, false));//丢弃手中的magicNumber张指定牌（最后一个参数决定是否随机选牌 false不随机 true随机）
        
        AbstractCard c = new 示例卡牌();                                                          //创建一个新的卡牌对象c为卡Modbasecard,可以用c.调用卡Modbasecard里面的方法、参数。格式： AbstractCard c = new 卡牌类名();不局限于和下面一句联用。
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
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(3);
            this.upgradeDamage(3);
            this.upgradeMagicNumber(1);
            this.upgradeBaseCost(1);//这个跟其他不一样，是升级后的消耗值
        }
    }
}