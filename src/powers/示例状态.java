package powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

/**
 * @deprecated
 */
public class 示例状态 extends AbstractPower {
	public static final String POWER_ID = "";
	public static final String NAME = "能力";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String DESCRIPTION = "能力描叙";
	public static final String[] DESCRIPTIONS = {"","",""};
	
	public 示例状态(AbstractCreature owner, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTION;
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}

}
