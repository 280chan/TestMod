
package deprecated.cards.colorless;

import basemod.abstracts.*;
import deprecated.powers.StasisFormPower;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;

/**
 * @deprecated
 */
public class StasisForm extends CustomCard {
	public static final String ID = "StasisForm";
	public static final String NAME = "凝滞形态";
	public static final String IMG = "resources/images/relic1.png";
	private static final String[] DESCRIPTIONS = {"每回合开始，从手牌中选择1张牌，", "使其耗能在本场战斗降低1，并", "将其复制品凝滞在当前血量最少的敌人身上。"};
	public static final String DESCRIPTION = DESCRIPTIONS[0] + DESCRIPTIONS[2];
	public static final String UPGRADED_DESCRIPTION = DESCRIPTIONS[0] + DESCRIPTIONS[1] + DESCRIPTIONS[2];
	
	private static final int COST = 3;// 卡牌费用

	public StasisForm() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.RARE, CardTarget.SELF);
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StasisFormPower(p, 1, this.upgraded), 1));
    }
    
    public AbstractCard makeCopy() {
        return new StasisForm();
    }//复制卡牌后复制的卡，如果卡组里有复制卡牌的卡每张卡都要有这个

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();//改名，其实就是多个+
            this.rawDescription = UPGRADED_DESCRIPTION;
            this.initializeDescription();
        }
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}