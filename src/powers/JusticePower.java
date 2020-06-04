
package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
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
	
	private Justice j;
	
	public JusticePower(AbstractCreature owner, Justice j) {
		this.j = j;
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
    		this.j.show();
    		this.addToTop(new ApplyPowerAction(t, t, new StrengthPower(t, 1), 1));
    	}
		return true;
	}

}
