
package powers;//包名，请根据自己的包路径修改，一般在创建类的时候自动填好。

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import mymod.TestMod;
import relics.Justice;

public class JusticePower extends AbstractPower implements OnReceivePowerPower, InvisiblePower {
	public static final String POWER_ID = "JusticePower";
	public static final String NAME = "正义";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String DESCRIPTION = "每当你获得负面状态时，增加 #b1 力量。";
	
	public JusticePower(AbstractCreature owner) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = -1;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTION;
	}
	
	@Override
	public boolean onReceivePower(AbstractPower p, AbstractCreature t, AbstractCreature source) {
		if (t.isPlayer && p.type == PowerType.DEBUFF) {
    		((Justice)AbstractDungeon.player.getRelic(TestMod.makeID(Justice.ID))).show();
    		AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(t, t, new StrengthPower(t, 1), 1));
    	}
		return true;
	}

}
