package powers;

import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

public class DefenceDownPower extends AbstractPower {
	public static final String POWER_ID = "DefenceDownPower";
	public static final String NAME = "受到伤害增加";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = { "受到伤害增加 #b", "% 。" };
	
	public DefenceDownPower(AbstractCreature owner, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.DEBUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
	}
	
	public void stackPower(final int stackAmount) {
		this.amount += stackAmount;
		this.fontScale = 8.0f;
	}
	
    public float atDamageReceive(float damage, DamageType damageType) {
        return damage / 100f * (100 + this.amount);
    }
    
}
