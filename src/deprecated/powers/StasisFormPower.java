package deprecated.powers;//包名，请根据自己的包路径修改，一般在创建类的时候自动填好。

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import deprecated.actions.StasisFormAction;

/**
 * @deprecated
 */
public class StasisFormPower extends AbstractPower{
	public static final String POWER_ID = "StasisFormPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "凝滞形态";//能力的名称。
	public static final String[] DESCRIPTIONS = {"每回合开始，从手牌中选择 #b", " 张牌，", "使其耗能在本场战斗变为 #b0 ，并", "将其复制品凝滞在当前血量最少的敌人身上。"};
	
	private boolean upgraded = false;
	
	public StasisFormPower(AbstractCreature owner, int amount, boolean upgraded) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.name = NAME;
		if (upgraded)
			this.name += "+";
		this.ID = POWER_ID + upgraded;
		this.owner = owner;
		this.amount = amount;
		this.upgraded = upgraded;

		updateDescription();//调用该方法（第36行）的文本更新函数,更新一次文本描叙，不可缺少。
		loadRegion("stasis");
		this.type = PowerType.BUFF;//能力种类，可以不填写，会默认为PowerType.BUFF。PowerType.BUFF不会被人工制品抵消，PowerType.DEBUFF会被人工制品抵消。
	}
	
	public void updateDescription() {
		this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];// 不需要调用变量的文本更新方式。
		if (this.upgraded)
			this.description += DESCRIPTIONS[2];
		this.description += DESCRIPTIONS[3];
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}//可通过添加if判定this.amount来限制层数上限。
	
    public void atStartOfTurnPostDraw() {
    	AbstractMonster target = null;
    	for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!m.isDead && !m.isDying && !m.halfDead) {
				if (target == null || target.currentHealth > m.currentHealth) {
					target = m;
				}
			}
		}
    	AbstractDungeon.actionManager.addToBottom(new StasisFormAction(this.amount, target, this.upgraded));
    }//触发时机：当玩家回合开始时触发。
    
}
