
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import actions.ArrangementAction;

import com.megacrit.cardcrawl.dungeons.*;
import com.badlogic.gdx.math.MathUtils;

public class Arrangement extends CustomCard {
    public static final String ID = "Arrangement";
    public static final String NAME = "安排一下";
    public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "获得X点 格挡 。造成X点伤害。抽X张牌。 升级 最多X张手牌中的牌，每少 升级 一张，获得 [R] 。 消耗 。";
    public static final String UPGRADED_DESCRIPTION = "获得 !B! 点 格挡 X次。造成 !D! 点伤害X次。抽X张牌。 升级 最多X张手牌中的牌，每少 升级 一张，获得 [R] 。 消耗 。";
    private static final int COST = -1;//卡牌费用
    private static final int BASE_BLK = 0;//基础格挡值
    private static final int BASE_DMG = 0;//基础伤害值
    private static final int BASE_MGC = 1;

    public Arrangement() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.RARE, CardTarget.SELF_AND_ENEMY);
        this.baseBlock = BASE_BLK;//基础格挡值. this.block为有敏捷等加成的格挡值.
        this.baseDamage = BASE_DMG;//基础伤害值. this.damage为有力量、钢笔尖等加成的伤害值.
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
        
        this.exhaust = true;//消耗属性，false不消耗，true消耗。可在该类里调用改变。不消耗就可以赋值为false或者删掉这一行
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ArrangementAction(p, m, this.damageTypeForTurn, this.freeToPlayOnce, this.upgraded, this.energyOnUse, this.damage, this.block));//将手牌中某张非技能卡复制magicNumber次
    }
    
	public void calculateCardDamage(AbstractMonster m) {
		AbstractPlayer player = AbstractDungeon.player;

		this.isBlockModified = false;
		float blc = this.baseBlock;

		this.isDamageModified = false;
		float tmp = this.baseDamage;

		if (!this.upgraded) {
			int x = EnergyPanel.totalCount;
			if (this.energyOnUse != -1)
				x = this.energyOnUse;
			if (player.hasRelic("Chemical X"))
				x += 2;
			blc = x;
			tmp = x;
		}

		// 防御
		for (AbstractPower p : player.powers) {
			blc = p.modifyBlock(blc);
			if (this.baseBlock != MathUtils.floor(blc)) {
				this.isBlockModified = true;
			}
		}
		if (blc < 0.0F) {
			blc = 0.0F;
		}
		this.block = MathUtils.floor(blc);

		// 攻击
		if ((AbstractDungeon.player.hasRelic("WristBlade")) && ((this.costForTurn == 0) || (this.freeToPlayOnce))) {
			tmp += 3.0F;
			if (this.baseDamage != (int) tmp) {
				this.isDamageModified = true;
			}
		}
		for (AbstractPower p : player.powers) {
			tmp = p.atDamageGive(tmp, this.damageTypeForTurn);
			if (this.baseDamage != (int) tmp) {
				this.isDamageModified = true;
			}
		}
		for (AbstractPower p : m.powers) {
			tmp = p.atDamageReceive(tmp, this.damageTypeForTurn);
		}
		for (AbstractPower p : player.powers) {
			tmp = p.atDamageFinalGive(tmp, this.damageTypeForTurn);
			if (this.baseDamage != (int) tmp) {
				this.isDamageModified = true;
			}
		}
		for (AbstractPower p : m.powers) {
			tmp = p.atDamageFinalReceive(tmp, this.damageTypeForTurn);
			if (this.baseDamage != (int) tmp) {
				this.isDamageModified = true;
			}
		}
		if (tmp < 0.0F) {
			tmp = 0.0F;
		}
		this.damage = MathUtils.floor(tmp);
	}
    
    public AbstractCard makeCopy() {
        return new Arrangement();
    }//复制卡牌后复制的卡，如果卡组里有复制卡牌的卡每张卡都要有这个

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();//改名，其实就是多个+
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
            this.upgradeBlock(1);//升级增加的护甲
            this.upgradeDamage(1);
        }
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}