package powers;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Reboot;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

public class ShutDownPower extends AbstractPower {
	public static final String POWER_ID = "ShutDownPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "关机";//能力的名称。
	public static final String[] DESCRIPTIONS = { "每回合将 #b", " 张 #y重启", " 放入手牌。" };
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	private boolean upgraded;
	
	public ShutDownPower(AbstractCreature owner, boolean upgraded) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.name = NAME;
		if (upgraded)
			this.name += "+";
		this.ID = POWER_ID + upgraded;
		this.owner = owner;
		this.amount = 1;
		this.upgraded = upgraded;
		this.img = ImageMaster.loadImage(IMG);
		//以上五句不可缺少，照抄即可。记得修改this.img的图片路径。
		updateDescription();//调用该方法（第36行）的文本更新函数,更新一次文本描叙，不可缺少。
		this.type = PowerType.BUFF;//能力种类，可以不填写，会默认为PowerType.BUFF。PowerType.BUFF不会被人工制品抵消，PowerType.DEBUFF会被人工制品抵消。
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
		 if (this.upgraded)
			 this.description += "+";
		 this.description += DESCRIPTIONS[2];
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}//可通过添加if判定this.amount来限制层数上限。
	
    public void atStartOfTurn() {
    	AbstractCard c = new Reboot();
    	if (upgraded)
    		c.upgrade();
		for (int i = 0; i < this.amount; i++) {
			AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(c.makeStatEquivalentCopy()));
		}
    }
    
}
