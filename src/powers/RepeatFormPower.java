package powers;//包名，请根据自己的包路径修改，一般在创建类的时候自动填好。

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

public class RepeatFormPower extends AbstractPower{
	public static final String POWER_ID = "RepeatFormPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "复读形态";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = {"每回合开始，将 #b"," 张 #y", " 加入手牌。"};
	
	private AbstractCard c;
	
	public RepeatFormPower(AbstractPlayer owner, int amount, AbstractCard card) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.c = card.makeStatEquivalentCopy();
		this.c.resetAttributes();
		this.name = NAME + "[" + c.name + "]";
		this.ID = POWER_ID + c.cardID + c.timesUpgraded + c.cost + c.magicNumber;
		this.owner = owner;
		this.amount = amount;

		updateDescription();//调用该方法（第36行）的文本更新函数,更新一次文本描叙，不可缺少。
		this.img = ImageMaster.loadImage(IMG);
		this.type = PowerType.BUFF;//能力种类，可以不填写，会默认为PowerType.BUFF。PowerType.BUFF不会被人工制品抵消，PowerType.DEBUFF会被人工制品抵消。
	}
	
	public void updateDescription() {
		this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.c.name + DESCRIPTIONS[2];
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}//可通过添加if判定this.amount来限制层数上限。
	
    public void atStartOfTurn() {
    	AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(this.c.makeStatEquivalentCopy(), this.amount));
    }//触发时机：当玩家回合开始时触发。
    
}
