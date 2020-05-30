
package deprecated.cards.colorless;

import basemod.abstracts.*;
import deprecated.actions.TreasureHuntActionOld;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import com.megacrit.cardcrawl.dungeons.*;

/**
 * @deprecated
 */
public class TreasureHunterOld extends CustomCard
{
    public static final String ID = "TreasureHunter";
    public static final String NAME = "宝藏猎手";
    public static final String IMG = "resources/images/relic1.png";
    public static final String DESCRIPTION = "从3张随机稀有牌中选择1张加入手牌。使这张牌的耗能在本局游戏中增加 !M! 。消耗。虚无。";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
    private static final int COST = 0;//卡牌费用
    
    public TreasureHunterOld source = null;
    
    public TreasureHunterOld() {
        this(COST);
    }//对卡牌进行初始化：        super("Modbasecard", "mod原卡", "图片路径", 1 , "全能test",AbstractCard.CardType.ATTACK, AbstractCardEnum.CYAN, AbstractCard.CardRarity.BASIC, AbstractCard.CardTarget.ENEMY);
     //参数分别对应是：                       ID      卡名      卡用的图    费用    卡牌说明      卡牌种类                      卡牌颜色             卡牌分类(也可以理解成稀有度)       卡牌目标
    
    public TreasureHunterOld(int cost) {
    	super(ID, NAME, IMG, cost, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.RARE, CardTarget.NONE);
    	this.exhaust = true;//消耗属性，false不消耗，true消耗。可在该类里调用改变。不消耗就可以赋值为false或者删掉这一行
        this.isEthereal = true;
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
    }
    
    private TreasureHunterOld(int cost, TreasureHunterOld source) {
    	this(cost);
    	this.source = source;
    }
    
    //卡牌种类（AbstractCard.CardType.SKILL，技能，AbstractCard.CardType.ATTACK，攻击，AbstractCard.CardType.POWER，能力）
    //卡牌分类（AbstractCard.CardRarity.BASIC,基础牌，AbstractCard.CardRarity.COMMON,白卡，AbstractCard.CardRarity.RARE,金卡，AbstractCard.CardRarity.UNCOMMON，蓝卡)
    //卡牌颜色（需要新建一个AbstractCardEnum类import后使用。代码如下：@SpireEnum /换行  public static AbstractCard.CardColor Color; Color为你的角色对应的颜色）
    //卡牌目标（就是选择了卡牌后能选的目标）（AbstractCard.CardTarget.SELF，自己，AbstractCard.CardTarget.SELF_AND_ENEMY，自己以及敌人【大概就是同时加盾同时攻击】，AbstractCard.CardTarget.ALL_ENEMY【所有敌人】，AbstractCard.CardTarget.ENEMY【敌人】）

    //下面这个use函数就是使用卡牌后会造成的效果
    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	AbstractDungeon.actionManager.addToBottom(new TreasureHuntActionOld(!this.canUpgrade(), this));
    }
    
    public AbstractCard makeCopy() {
		return new TreasureHunterOld(this.cost, this);
    }//复制卡牌后复制的卡，如果卡组里有复制卡牌的卡每张卡都要有这个

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();//改名，其实就是多个+
            this.rawDescription = "从3张随机稀有牌中选择1张加入手牌，并加入牌库。使这张牌的耗能在本局游戏中增加 !M! 。消耗。虚无。";
            initializeDescription();
        }
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}