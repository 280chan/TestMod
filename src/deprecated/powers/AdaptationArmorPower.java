package deprecated.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

/**
 * @deprecated
 */
public class AdaptationArmorPower extends AbstractPower {
	public static final String POWER_ID = "AdaptationArmorPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "适应装甲";//能力的名称。
    public static final String IMG = "resources/images/relic1.png";
	public static final String[] DESCRIPTIONS = {"减少 #b","% 受到的 #y攻击 。若完全格挡一次 #y攻击 ，将当前减伤比例 #r减半 ，否则再减少 #b10% ( #y乘算 )。"};//需要调用变量的文本描叙，例如力量（Strength）、敏捷（Dexterity）等。
	
	private float rate;
	private int percent;
	
	public AdaptationArmorPower(AbstractCreature owner, int amount, int percent) {
		this.name = NAME + "(递减" + percent + "%)";
		this.ID = POWER_ID + percent;
		this.owner = owner;
		this.percent = percent;
		this.rate = amount / 100f;
		this.updateAmount();
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.BUFF;
	}

	private String getAmount() {
		return "" + ((int) (rate * 10000)) / 100f;
	}
	
	private void updateAmount() {
		this.amount = (int) (rate * 100);
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.getAmount() + DESCRIPTIONS[1];//不需要调用变量的文本更新方式。
	}
	
	public void stackPower(final int amount) {
		this.fontScale = 8.0f;
		this.increaseRate(amount / 100f);
		this.updateAmount();
	}
	
	private void increaseRate(float rate) {
		this.rate = 1 - (1 - this.rate) * (1 - rate);
	}
    
	public int onAttacked(DamageInfo info, int amount) {
		if (info.type == DamageType.NORMAL) {
			if (amount > 0) {
				this.increaseRate(percent / 100f);
				this.flash();
			} else {
				this.rate /= 2;
			}
			this.updateAmount();
			this.updateDescription();
		}
		return amount;
	}
    
    public float atDamageFinalReceive(final float damage, final DamageType type) {//参数：damage-伤害数值，damageType-伤害种类
        if (type == DamageType.NORMAL)
        	return damage * (1 - this.rate);
        return damage;
    }
    
}
