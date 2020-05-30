package deprecated.powers;

import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import actions.LackOfCardAction;
import utils.MiscMethods;

/**
 * @deprecated
 */
public class LackOfCardPower extends AbstractPower implements MiscMethods {
	public static final String POWER_ID = "LackOfCardPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "均衡调节";//能力的名称。
    public static final String IMG = "resources/images/LackOfCardPower.png";
	public static final String[] DESCRIPTIONS = {"回合开始时，如果手牌数不超过当前能量，额外抽 #b", " 张牌，否则获得", "。"};//需要调用变量的文本描叙，例如力量（Strength）、敏捷（Dexterity）等。
	//以上两种文本描叙只需写一个，更新文本方法在第36行。
	
	public LackOfCardPower(AbstractCreature owner, int amount) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		//以上五句不可缺少，照抄即可。记得修改this.img的图片路径。
		updateDescription();//调用该方法（第36行）的文本更新函数,更新一次文本描叙，不可缺少。
		this.type = PowerType.BUFF;//能力种类，可以不填写，会默认为PowerType.BUFF。PowerType.BUFF不会被人工制品抵消，PowerType.DEBUFF会被人工制品抵消。
	}

	private String setDescription(PlayerClass c) {
		return this.setDescription(c, DESCRIPTIONS[0]+ this.amount + DESCRIPTIONS[1], DESCRIPTIONS[2]);
	}
	
	public void updateDescription() {
		 this.description = this.setDescription(AbstractDungeon.player.chosenClass);
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}
	
	public void atStartOfTurnPostDraw() {
		AbstractDungeon.actionManager.addToBottom(new LackOfCardAction(AbstractDungeon.player, this.amount));
	}
	
}
