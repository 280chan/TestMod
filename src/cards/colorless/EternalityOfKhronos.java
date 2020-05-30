
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;
import utils.MiscMethods;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

public class EternalityOfKhronos extends CustomCard implements MiscMethods {
    public static final String ID = "EternalityOfKhronos";
    public static final String NAME = "柯罗诺斯的永恒";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "立即结束你的回合，敌人在这回合不会执行其意图，然后开始你的下一回合。 虚无 。";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
    public static final String DESCRIPTION1 = " 消耗 。";
    private static final int COST = 2;//卡牌费用

    public EternalityOfKhronos() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION + DESCRIPTION1, CardType.SKILL, CardColor.COLORLESS, CardRarity.RARE, CardTarget.NONE);
        this.isEthereal = true;
        this.exhaust = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.turnSkipperStartByCard(this);
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.exhaust = false;
            this.rawDescription = DESCRIPTION;
            this.initializeDescription();
        }
    }
}