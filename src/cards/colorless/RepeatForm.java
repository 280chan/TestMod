
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.RepeatFormAction;

import com.megacrit.cardcrawl.dungeons.*;

public class RepeatForm extends CustomCard {
	public static final String ID = "RepeatForm";
	public static final String NAME = "复读形态";
	public static final String IMG = TestMod.cardIMGPath("relic1");
	public static final String UPGRADE_DESCRIPTION = "选择一张牌从本场战斗中移除。每回合将1张其复制品加入手牌。";
	public static final String DESCRIPTION = UPGRADE_DESCRIPTION + " 虚无 。";
	
	private static final int COST = 3;// 卡牌费用

	public RepeatForm() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.RARE, CardTarget.SELF);
        this.isEthereal = true;
	}

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	AbstractDungeon.actionManager.addToBottom(new RepeatFormAction(this));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();//改名，其实就是多个+
            this.rawDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
            this.isEthereal = false;
        }
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}