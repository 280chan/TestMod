
package deprecated.powers;//包名，请根据自己的包路径修改，一般在创建类的时候自动填好。

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.SlowPower;

import relics.BlackFramedGlasses;

/**
 * @deprecated
 */
public class BlackFramedGlassesPower extends AbstractPower{
	
	public static final String POWER_ID = "BlackFramedGlassesPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "黑框眼镜";//能力的名称。
	
	public static final String DESCRIPTION = "每场战斗有一次机会，受到致命伤时恢复到 #b1 点生命，并给予所有敌人缓慢。";//不需要调用变量的文本描叙，例如钢笔尖（PenNibPower）。
	public static final String[] DESCRIPTIONS = {"","",""};//需要调用变量的文本描叙，例如力量（Strength）、敏捷（Dexterity）等。
	//以上两种文本描叙只需写一个，更新文本方法在第36行。
	
	private static BlackFramedGlasses b;
	
	public static void setGlasses(BlackFramedGlasses b) {
		BlackFramedGlassesPower.b = b;
	}
	
	public BlackFramedGlassesPower(AbstractCreature owner) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.img = ImageMaster.loadImage("resources/images/relic1.png");
		//以上五句不可缺少，照抄即可。记得修改this.img的图片路径。
		updateDescription();//调用该方法（第36行）的文本更新函数,更新一次文本描叙，不可缺少。
		this.type = PowerType.BUFF;//能力种类，可以不填写，会默认为PowerType.BUFF。PowerType.BUFF不会被人工制品抵消，PowerType.DEBUFF会被人工制品抵消。
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTION;//不需要调用变量的文本更新方式。
		 
		 //this.description = (DESCRIPTIONS[0] + 变量1 + DESCRIPTIONS[1] + 变量2 + DESCRIPTIONS[3] + ······);需要调用变量的文本更新方式。
		 //例： public static final String[] DESCRIPTIONS = {"在你回合开始时获得","点力量."};
		 //   this.description = (DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1]);
		 //   通过该方式更新后的文本:在你回合开始时获得amount层力量.
		 //   另外一提，除变量this.amount（能力层数对应的变量）外，其他变量被赋值后需要人为调用updateDescription函数进行文本更新。
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}//可通过添加if判定this.amount来限制层数上限。
	
	//触发时机，以下部分触发时机仅翻译英文，具体效果未知，如有错误，请见谅。如仍无法满足DIY需求，请详参desktop的各卡牌源码或AbstractCard类的源码。
	//小tips：在以下触发时机里，需要的闪烁的，可调用flash();让能力闪一下.
	
    public float atDamageGive(final float damage, final DamageInfo.DamageType type) {//参数：damage-伤害数值，type-伤害种类
        return damage;//该damage即卡牌类里面的this.damage。
    }//触发时机：给予伤害值时，返回伤害值，可用来修改伤害数值，会重复触发，请不要在该函数里调用ApplyPowerAction、DamageAction等一系列Action，只可对damage进行运算（可以进行条件判定）。
    //如需调用Action，请使用下文写出的onAttack。
    
    public void onAttack(final DamageInfo info, final int damageAmount, final AbstractCreature target) {//参数： info-伤害信息，damageAmount-伤害数值，target-伤害目标
    }//触发时机：当玩家攻击时。info.可调用伤害信息。
    
    public float atDamageReceive(final float damage, final DamageInfo.DamageType damageType) {//参数：damage-伤害数值，damageType-伤害种类
        return damage;//该damage指被格挡前的damage。
    }//触发时机：当玩家受到伤害时，返回伤害数值，可用来修改伤害数值，会重复触发，请不要在该函数里调用ApplyPowerAction、DamageAction等一系列Action，只可对damage进行运算（可以进行条件判定）。
    //如需调用Action，请使用下文的onAttacked。
    
    public int onAttacked(final DamageInfo info, final int damageAmount) {//参数：info-伤害信息，damageAmount-伤害数值
        return damageAmount;//该damaeAmount为未被格挡的伤害（参考遗物鸟居）。
    }//触发时机：当玩家被攻击时，返回伤害数值，可用来修改伤害数值。info.可调用伤害信息。
    
    //伤害信息：info.owner (该次伤害的攻击者) info.type(该次伤害的种类，可利用info.type.调用伤害种类)
    //伤害种类:DamageInfo.DamageType.HP_LOSS (失去生命，无法被格挡，无法触发原版的【受到伤害时】的条件)  DamageInfo.DamageType.NORMAL (一般伤害，可以被格挡，能触发原版的【受到伤害时】的条件)  DamageInfo.DamageType.THORNS (荆棘伤害，可以被格挡，无法触发原版的【受到伤害时】的条件)
    
    public void atStartOfTurn() {
    }//触发时机：当玩家回合开始时触发。
    
    public void atEndOfTurn(final boolean isPlayer) {
    }//触发时机：当玩家回合结束时触发。
    
    public void atEndOfRound() {
    }//触发时机：当怪物回合结束时触发。
    
    public void onDamageAllEnemies(final int[] damage) {
    }//触发时机：当对敌人全体造成伤害时触发。（待商榷，未使用过）
    
    public int onHeal(final int healAmount) {
        return healAmount;
    }//触发时机：当玩家回复生命时，返回生命回复数值，可以用来修改生命回复数值。
    
    public void onUseCard(final AbstractCard card, final UseCardAction action) {
    }//触发时机：当一张卡被打出且卡牌效果生效前。
    
    public void onPlayCard(final AbstractCard card, final AbstractMonster m) {
    }//触发时机：当一张卡被打出且卡牌效果生效前。
    
    public void onAfterUseCard(final AbstractCard card, final UseCardAction action) {
    }//触发时机：当一张卡被打出后进入弃牌堆/消耗堆时。
    
    public void onAfterCardPlayed(final AbstractCard usedCard) {
    }//触发时机：当一张卡被打出且卡牌效果生效后。
    
    public void atEnergyGain() {
    }//触发时机：当玩家获得能量时。
    
    public void onExhaust(final AbstractCard card) {
    }//触发时机：当玩家消耗卡牌时。
    
    public void onGainedBlock(final float blockAmount) {
    }//触发时机：当玩家获得格挡时。
    
    public void onRemove() {
    }//触发时机：当能力被移除时。
    
    public void onApplyPower(final AbstractPower power, final AbstractCreature target, final AbstractCreature source) {
    	//参数：power-能力Id，target-被给予者，source-给予者
    }//触发时机：当给予能力时。
    
    public int onLoseHp(final int damageAmount) {
    	if (AbstractDungeon.player.currentHealth <= damageAmount) {
			MonsterGroup mg = AbstractDungeon.getCurrRoom().monsters;
			if (mg != null && mg.monsters != null) {
				for (AbstractMonster m : mg.monsters) {
					if (!m.isDead && !m.isDying) {
						m.addPower(new SlowPower(m, 1));
					}
				}
			}
			b.flash();
			AbstractDungeon.actionManager.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, b));
			AbstractDungeon.player.currentHealth = 1;
			AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, "BlackFramedGlassesPower"));
			return 0;
		}
        return damageAmount;
    }//触发时机：当失去生命值时，返回伤害数值，可用来修改伤害数值。
    
    public void onVictory() {
    }//触发时机：当一个房间获胜时。

}
