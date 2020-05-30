package powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

public class SuperconductorPower extends AbstractPower {
	public static final String POWER_ID = "SuperconductorPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "抽牌降费";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = {"接下来抽到的前 #b", " 张耗能大于 #b0 的牌的耗能在回合内降低 #b1 。"};
	
	public SuperconductorPower(AbstractCreature owner, int amount) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();//调用该方法（第36行）的文本更新函数,更新一次文本描叙，不可缺少。
		this.type = PowerType.BUFF;//能力种类，可以不填写，会默认为PowerType.BUFF。PowerType.BUFF不会被人工制品抵消，PowerType.DEBUFF会被人工制品抵消。
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];//不需要调用变量的文本更新方式。
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
		this.amount += stackAmount;
	}
    
	public void onCardDraw(AbstractCard c) {
		if (c.costForTurn > 0 && !c.freeToPlayOnce && this.amount > 0) {
			c.setCostForTurn(c.costForTurn - 1);
			this.amount--;
			if (this.amount == 0) {
				AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(owner, owner, this));
			} else {
				this.updateDescription();
			}
		}
	}
    
}
