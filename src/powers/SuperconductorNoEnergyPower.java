package powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import mymod.TestMod;

public class SuperconductorNoEnergyPower extends AbstractPower {
	public static final String POWER_ID = "SuperconductorNoEnergyPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "无法获得能量";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String DESCRIPTION = "本回合内，无法获得能量。";
	private int original;
	
	public SuperconductorNoEnergyPower(AbstractCreature owner, int original) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = -1;
		this.original = original;
		this.checkZero();
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();//调用该方法（第36行）的文本更新函数,更新一次文本描叙，不可缺少。
		this.type = PowerType.DEBUFF;//能力种类，可以不填写，会默认为PowerType.BUFF。PowerType.BUFF不会被人工制品抵消，PowerType.DEBUFF会被人工制品抵消。
	}
	
	public static void UpdateCurrentInstance() {
		if (!AbstractDungeon.player.hasPower(POWER_ID))
			return;
		((SuperconductorNoEnergyPower) AbstractDungeon.player.getPower(POWER_ID)).checkEnergy();
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTION;//不需要调用变量的文本更新方式。
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
    
    public void atEndOfTurn(final boolean isPlayer) {
    	AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(owner, owner, this));
    }

    public void checkEnergy() {
    	if (EnergyPanel.totalCount > this.original) {
    		EnergyPanel.totalCount = this.original;
    	} else if (EnergyPanel.totalCount < this.original) {
    		this.original = EnergyPanel.totalCount;
    		this.checkZero();
    	}
    }
    
    private void checkZero() {
		if (this.original < 0)
			this.original = 0;
    }
    
}
