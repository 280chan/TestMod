
package powers;//包名，请根据自己的包路径修改，一般在创建类的时候自动填好。

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import mymod.TestMod;

public class GreedPower extends AbstractPower{
	
	public static final String POWER_ID = "GreedPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "贪婪";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String DESCRIPTION = "回合结束时，每有一点能量剩余，下回合少抽一张牌；每有一张牌剩余，下回合减少一点能量。";//不需要调用变量的文本描叙，例如钢笔尖（PenNibPower）。
	
	public GreedPower(AbstractCreature owner, int amount) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();//调用该方法（第36行）的文本更新函数,更新一次文本描叙，不可缺少。
		this.type = PowerType.BUFF;//能力种类，可以不填写，会默认为PowerType.BUFF。PowerType.BUFF不会被人工制品抵消，PowerType.DEBUFF会被人工制品抵消。
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTION;
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}//可通过添加if判定this.amount来限制层数上限。
	
    public void atEndOfTurn(final boolean isPlayer) {
    	AbstractPlayer p = AbstractDungeon.player;
    	int handLeft = p.hand.size();
    	int energyLeft = EnergyPanel.totalCount;
    	for (int i = 0; i < this.amount; i++) {
    		if (handLeft > 0)
    			AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new DischargePower(p, handLeft), handLeft));
    		if (energyLeft > 0)
    			AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new DrawDownPower(p, energyLeft), energyLeft));
    	}
    	AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, "GreedPower"));
    }//触发时机：当玩家回合结束时触发。
    
}
