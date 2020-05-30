package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import actions.DorothysBlackCatDamageAction;
import mymod.TestMod;

public class DorothysBlackCatEnemyPower extends AbstractPower implements InvisiblePower {
	public static final String POWER_ID = "DorothysBlackCatEnemyPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "桃乐丝的黑猫";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = {"每当你对 #y", " 造成 #r失去生命  #y以外类型 的伤害时，使所有敌人均摊失去总共 #b","% 其数值的生命。"};//需要调用变量的文本描叙，例如力量（Strength）、敏捷（Dexterity）等。
	
	public DorothysBlackCatEnemyPower(AbstractCreature owner, int amount) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();//调用该方法（第36行）的文本更新函数,更新一次文本描叙，不可缺少。
		this.type = PowerType.DEBUFF;//能力种类，可以不填写，会默认为PowerType.BUFF。PowerType.BUFF不会被人工制品抵消，PowerType.DEBUFF会被人工制品抵消。
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.owner.name + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}
	
    public int onAttacked(final DamageInfo info, final int damage) {//参数： info-伤害信息，damageAmount-伤害数值，target-伤害目标
    	if (info.type != DamageType.HP_LOSS && damage * this.amount / 100f > 0) {
    		AbstractDungeon.actionManager.addToBottom(new DorothysBlackCatDamageAction(damage * this.amount / 100f));
    	}
    	return damage;
    }
    
}
