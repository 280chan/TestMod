package powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

public class OneHitWonderDebuffPower extends AbstractPower {
	public static final String POWER_ID = "OneHitWonderDebuffPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "一血传奇";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String DESCRIPTION = "当玩家生命值为 #b1 时，造成的伤害降低 #b50% ，受到的攻击伤害和未被格挡的其余伤害增加 #b50% 。";//不需要调用变量的文本描叙，例如钢笔尖（PenNibPower）。
	
	public OneHitWonderDebuffPower(AbstractCreature owner) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = -1;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();//调用该方法（第36行）的文本更新函数,更新一次文本描叙，不可缺少。
		this.type = PowerType.DEBUFF;//能力种类，可以不填写，会默认为PowerType.BUFF。PowerType.BUFF不会被人工制品抵消，PowerType.DEBUFF会被人工制品抵消。
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTION;//不需要调用变量的文本更新方式。
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	private static boolean checkPlayerHealth() {
		return AbstractDungeon.player.currentHealth == 1;
	}
	
	public int onAttacked(DamageInfo info, int damage) {
		if (checkPlayerHealth() && info.type != DamageType.NORMAL)
			return (int)(1.5f * damage);
		return damage;
	}
	
	public float atDamageFinalGive(float damage, DamageType type) {
		if (checkPlayerHealth())
			return 0.5f * damage;
		return damage;
	}
    
	public float atDamageFinalReceive(float damage, DamageType type) {
		if (checkPlayerHealth() && type == DamageType.NORMAL)
			return 1.5f * damage;
		return damage;
	}
    
}
