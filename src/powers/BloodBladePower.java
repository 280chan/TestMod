package powers;//包名，请根据自己的包路径修改，一般在创建类的时候自动填好。

import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

public class BloodBladePower extends AbstractPower{
	public static final String POWER_ID = "BloodBladePower";
	public static final String NAME = "血气之刃";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = {"每次失去生命时，额外提升 #b失去生命/最大生命 的攻击伤害。提升之间相", " #y加 ", " #y乘 ", "。", " NL 当前提升: #b"};
	private boolean upgraded = false;
	private float bonus = 0;
	
	public BloodBladePower(AbstractCreature owner, boolean upgraded) {
		this.name = NAME;
		if (upgraded)
			this.name += "+";
		this.ID = POWER_ID + upgraded;
		this.owner = owner;
		this.amount = -1;
		this.img = ImageMaster.loadImage(IMG);
		this.upgraded = upgraded;
		this.onFirstGain();
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0];
		 if (upgraded) {
			 this.description += DESCRIPTIONS[2];
		 } else {
			 this.description += DESCRIPTIONS[1];
		 }
		 this.description += DESCRIPTIONS[3];
		 if (this.bonus > 0) {
			 double tmp = (((int)(this.bonus * 10000 + 0.5)) / 100.0);
			 this.description += DESCRIPTIONS[4] + (tmp) + "% ";
		 }
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}//可通过添加if判定this.amount来限制层数上限。
	
	public static boolean hasThis(boolean upgraded) {
		return AbstractDungeon.player.hasPower(POWER_ID + upgraded);
	}
	
	public static BloodBladePower getThis(boolean upgraded) {
		return (BloodBladePower) AbstractDungeon.player.getPower(POWER_ID + upgraded);
	}
	
    public float atDamageGive(final float damage, final DamageType type) {//参数：damage-伤害数值，type-伤害种类
        if (type != DamageType.HP_LOSS)
        	return damage * (1 + this.bonus);
        return damage;
    }//触发时机：给予伤害值时，返回伤害值，可用来修改伤害数值，会重复触发，请不要在该函数里调用ApplyPowerAction、DamageAction等一系列Action，只可对damage进行运算（可以进行条件判定）。
    //如需调用Action，请使用下文写出的onAttack。
    
    public void onFirstGain() {
    	this.increaseRate(1 - (1.0f * owner.currentHealth / owner.maxHealth));
    }
    
    private void increaseRate(float rate) {
    	if (this.upgraded) {
    		this.bonus = (1 + this.bonus) * (1 + rate) - 1;
    	} else {
    		this.bonus += rate;
    	}
    	this.updateDescription();
    }
    
    public int onLoseHp(final int damage) {
    	if (damage > 0) {
        	this.increaseRate(damage * 1.0f / owner.maxHealth);
    	}
        return damage;
    }//触发时机：当失去生命值时，返回伤害数值，可用来修改伤害数值。
    
}
