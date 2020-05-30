
package deprecated.cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;

/**
 * @deprecated
 */
public class ColorfulAttack extends CustomCard {
	public static final String ID = "ColorfulAttack";
    public static final String NAME = "缤纷攻击";
    public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "造成 !D! 点伤害 !M! 次。你每有一张不同的牌，伤害提高1。伤害次数提高数量最多的非基础牌的数量。";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
    private static final int COST = 3;//卡牌费用
    private static final int BASE_DMG = 0;//基础伤害值
    private static final int BASE_MGC = 0;

    public ColorfulAttack() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.RARE, CardTarget.ENEMY);
        this.baseDamage = BASE_DMG;//基础伤害值. this.damage为有力量、钢笔尖等加成的伤害值.
        this.baseMagicNumber = BASE_MGC;//特殊值，一般用来叠BUFF层数。和下一行连用。
        this.magicNumber = this.baseMagicNumber;
    	this.misc = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.applyPowers();
    	for (int i = 0; i < this.magicNumber; i++) {
    		AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AttackEffect.SLASH_HORIZONTAL));
    	}
    }
    
    public void countCards() {//构造一个静态函数countCards。返回值类型为int型。
        ArrayList<AbstractCard> list = new ArrayList<AbstractCard>();
        list.addAll(AbstractDungeon.player.hand.group);
        list.addAll(AbstractDungeon.player.drawPile.group);
        list.addAll(AbstractDungeon.player.discardPile.group);
        int count = 0, max = 0;
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> nonBasicIds = new ArrayList<String>();
    	for (AbstractCard c : list) {
            boolean had = false;
        	for (String id : ids)
            	if (id.equals(c.cardID))
            		had = true;
        	if (had)
        		continue;
    		ids.add(c.cardID);
            count++;
            if (c.rarity != CardRarity.BASIC)
            	nonBasicIds.add(c.cardID);
        }
    	
        this.baseDamage = count;
        
        for (String id : nonBasicIds) {
        	count = 0;
        	for (AbstractCard c : list)
        		if (id.equals(c.cardID))
        			count++;
        	if (count > max)
        		max = count;
        }
        if (this.magicNumber != max + this.misc) {
        	this.upgradeMagicNumber(max + this.misc - this.magicNumber);
        }
    }

    public void applyPowers() {
    	this.countCards();
    	super.applyPowers();
    }
    
    public AbstractCard makeCopy() {
        return new ColorfulAttack();
    }//复制卡牌后复制的卡，如果卡组里有复制卡牌的卡每张卡都要有这个

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();//改名，其实就是多个+
            this.upgradeMagicNumber(1);//升级增加的特殊常量MagicNumber
            this.misc++;
        }
    }
}