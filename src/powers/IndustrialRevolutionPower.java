
package powers;//包名，请根据自己的包路径修改，一般在创建类的时候自动填好。

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;
import relics.IndustrialRevolution;

public class IndustrialRevolutionPower extends AbstractPower implements OnReceivePowerPower, InvisiblePower {
	public static final String POWER_ID = "IndustrialRevolutionPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "工业革命";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = {"不会受到被标记的敌人施加的负面状态。"};//不需要调用变量的文本描叙，例如钢笔尖（PenNibPower）。

	private static boolean check(AbstractCreature m) {
		return IndustrialRevolution.LIST.contains(m);
	}
	
	public IndustrialRevolutionPower(AbstractCreature owner) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = -1;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0];//不需要调用变量的文本更新方式。
	}
	
	@Override
	public boolean onReceivePower(AbstractPower p, AbstractCreature t, AbstractCreature s) {
		if (t.isPlayer && check(s) && p.type == PowerType.DEBUFF) {
			return false;
		}
		return true;
	}
    
}
