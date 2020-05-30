package powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;
import utils.MiscMethods;

public class ReflectPower extends AbstractPower implements MiscMethods {
	public static final String POWER_ID = "ReflectPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "反射";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = {"在这个回合，你的下 #b"," 张非 #y诅咒 、 #y状态 的牌会打出两次。"};//需要调用变量的文本描叙，例如力量（Strength）、敏捷（Dexterity）等。
	//以上两种文本描叙只需写一个，更新文本方法在第36行。
	
	public ReflectPower(AbstractCreature owner, int amount) {//参数：owner-能力施加对象、amount-施加能力层数。在cards的use里面用ApplyPowerAction调用进行传递。
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];//不需要调用变量的文本更新方式。
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}
    
    public void atEndOfTurn(final boolean isPlayer) {
    	if (isPlayer) {
    	    AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    	}
    }
    
    private static boolean checkType(CardType t) {
    	if (t == CardType.CURSE)
    		return false;
    	if (t == CardType.STATUS)
    		return false;
    	return true;
    }
    
	public void onUseCard(AbstractCard card, UseCardAction action) {
		if ((!card.purgeOnUse) && checkType(card.type) && (this.amount > 0)) {
			flash();
			AbstractMonster m = null;
			if (action.target != null) {
				m = (AbstractMonster) action.target;
			}
			this.playAgain(card, m);
			if (--this.amount == 0) {
				AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this));
			} else {
				this.updateDescription();
			}
		}
	}

}
