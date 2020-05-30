
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.AngryPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;

public class Provocation extends CustomCard {
    public static final String ID = "Provocation";
    public static final String NAME = "挑衅";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "获得 !M! 点 力量 ，使敌人获得1层生气。";
    private static final int COST = 1;//卡牌费用
    private static final int BASE_MGC = 3;
    
    public Provocation() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
    }

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber));
		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new AngryPower(m, 1), 1, true));
	}
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();//改名，其实就是多个+
            this.upgradeMagicNumber(1);//升级增加的特殊常量MagicNumber
        }
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}