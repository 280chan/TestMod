
package powers;//包名，请根据自己的包路径修改，一般在创建类的时候自动填好。

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

public class DischargePower extends AbstractPower{
	
	public static final String POWER_ID = "DischargedPower";
	public static final String NAME = "能量减少";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String DESCRIPITON = "";
	public static final String[] DESCRIPTIONS = { "在下一回合，少获得", "点能量", "" };
	
	public DischargePower(AbstractCreature owner, int amount) {
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
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}
    
    public void atEnergyGain() {
    	AbstractDungeon.actionManager.addToBottom(new LoseEnergyAction(this.amount));
    	AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, "DischargedPower"));
    }

}
