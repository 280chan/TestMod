
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;
import powers.EnhanceArmermentPower;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;

public class EnhanceArmerment extends CustomCard {
    public static final String ID = "EnhanceArmerment";
    public static final String NAME = "完全支配";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "你打出的下一张攻击牌的伤害翻倍，但其将被 消耗 。";
    private static final int COST = 1;//卡牌费用

    public EnhanceArmerment() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.RARE, CardTarget.NONE);
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new EnhanceArmermentPower(p)));
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(0);
        }
    }
}