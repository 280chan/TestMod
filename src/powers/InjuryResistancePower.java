package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;
import relics.InjuryResistance;

public class InjuryResistancePower extends AbstractPower implements InvisiblePower {
	public static final String POWER_ID = "InjuryResistancePower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "伤害抗性";//能力的名称。
	public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = {"每当受到伤害后，接下来受到的伤害的最终数值都将降低 #b1 。 NL 当通过此状态完全格挡一次伤害后，数值重置。 NL 下次伤害最终数值降低 #b"," 。"};//需要调用变量的文本描叙，例如力量（Strength）、敏捷（Dexterity）等。

	private InjuryResistance r;
	
	public InjuryResistancePower(AbstractCreature owner, InjuryResistance r) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = r.counter;
		this.r = r;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();//调用该方法（第36行）的文本更新函数,更新一次文本描叙，不可缺少。
		this.type = PowerType.BUFF;//能力种类，可以不填写，会默认为PowerType.BUFF。PowerType.BUFF不会被人工制品抵消，PowerType.DEBUFF会被人工制品抵消。
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];//不需要调用变量的文本更新方式。
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
		this.amount += stackAmount;
		this.r.counter = this.amount;
	}
	
    public int onLoseHp(int damage) {
    	if (this.amount > 0) {
    		damage -= this.amount;
    		if (damage <= 0) {
    			damage = 0;
    			this.amount = 0;
    		} else {
    			this.amount++;
    		}
    		this.r.show();
    	} else {
    		this.amount++;
    	}
		this.r.counter = this.amount;
		this.updateDescription();
        return damage;
    }
    
}
