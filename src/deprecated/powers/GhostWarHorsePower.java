package deprecated.powers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import deprecated.actions.GhostWarHorseAction;
/**
 * @deprecated
 */
public class GhostWarHorsePower extends AbstractPower {
	public static final String POWER_ID = "GhostWarHorsePower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "幽灵战马";//能力的名称。
    public static final String IMG = "resources/images/relic1.png";
	public static final String[] DESCRIPTIONS = {"每当你打出能力牌：直接获得 #b1 层 #y幽魂形态 ； NL 技能牌：如果有 #y幽魂形态 ，降低 #b1 层并减少 #b1 点 #y敏捷 ； NL 攻击牌，获得 #y幽魂形态 层数","点 #y敏捷。"};//需要调用变量的文本描叙，例如力量（Strength）、敏捷（Dexterity）等。
	//以上两种文本描叙只需写一个，更新文本方法在第36行。
	
	public GhostWarHorsePower(AbstractCreature owner, int amount) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();//调用该方法（第36行）的文本更新函数,更新一次文本描叙，不可缺少。
		this.type = PowerType.BUFF;//能力种类，可以不填写，会默认为PowerType.BUFF。PowerType.BUFF不会被人工制品抵消，PowerType.DEBUFF会被人工制品抵消。
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0];//不需要调用变量的文本更新方式。
		 if (this.amount > 0)
			 this.description += "+ #b" + this.amount + " ";
		 this.description += DESCRIPTIONS[1];
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}
	
	public void onAfterCardPlayed(final AbstractCard usedCard) {
		AbstractDungeon.actionManager.addToBottom(new GhostWarHorseAction(usedCard, this.amount));
    }
	
}
