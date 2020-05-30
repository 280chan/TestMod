package powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

public class EnhanceArmermentPower extends AbstractPower {
	public static final String POWER_ID = "EnhanceArmermentPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "完全支配";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String DESCRIPTION = "你打出的下一张攻击牌的伤害翻倍，但其将被消耗。";
	private static int idPostfix = 0;
	
	public EnhanceArmermentPower(AbstractCreature owner) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.name = NAME;
		this.ID = POWER_ID + idPostfix++;
		this.owner = owner;
		this.amount = -1;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();//调用该方法（第36行）的文本更新函数,更新一次文本描叙，不可缺少。
		this.type = PowerType.BUFF;//能力种类，可以不填写，会默认为PowerType.BUFF。PowerType.BUFF不会被人工制品抵消，PowerType.DEBUFF会被人工制品抵消。
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTION;//不需要调用变量的文本更新方式。
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}

	public float atDamageFinalGive(final float damage, final DamageType type) {//参数：damage-伤害数值，type-伤害种类
        if (type == DamageType.NORMAL)
        	return damage * 2f;
		return damage;
    }
    
    public void onUseCard(final AbstractCard card, final UseCardAction action) {
    	if (card.type == CardType.ATTACK) {
    		action.exhaustCard = true;
    		AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
    	}
    }
    
}
