
package powers;//包名，请根据自己的包路径修改，一般在创建类的时候自动填好。

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;
import relics.IndustrialRevolution;

public class InorganicPower extends AbstractPower implements OnReceivePowerPower {
	
	public static final String POWER_ID = "InorganicPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "无机物";//能力的名称。
	public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = {"无法给 #y", " 增益状态，无法给玩家负面状态。"};//不需要调用变量的文本描叙，例如钢笔尖（PenNibPower）。

	private static boolean check(AbstractCreature m) {
		return IndustrialRevolution.LIST.contains(m);
	}
	
	public InorganicPower(AbstractCreature owner) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = -1;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.DEBUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.owner.name + DESCRIPTIONS[1];//不需要调用变量的文本更新方式。
	}
	
	@Override
	public boolean onReceivePower(AbstractPower p, AbstractCreature t, AbstractCreature s) {
		if (check(s) && t.equals(this.owner) && s.equals(this.owner) && p.type == PowerType.BUFF) {
			if (p.ID.equals("Mode Shift"))
				return true;
			return false;
		}
		return true;
	}
    
}
