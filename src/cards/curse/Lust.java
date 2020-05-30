
package cards.curse;

import basemod.abstracts.*;
import mymod.TestMod;
import relics.Prudence;
import relics.Sins;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.FocusPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;

public class Lust extends CustomCard {
	public static final String ID = "Lust";
    public static final String NAME = "色欲";
    public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = " 不能被打出 。回合结束时，失去1 力量 、1 敏捷 、1 集中 ，获得3层 易伤 、3层 虚弱 、3层 脆弱 。";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
    private static final int COST = -2;//卡牌费用

    public Lust() {
    	super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.CURSE, CardColor.CURSE, CardRarity.SPECIAL, CardTarget.NONE);
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
	}
    
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
    	return p.hasRelic(Prudence.ID) || p.hasRelic("Blue Candle");
	}

	public void triggerOnEndOfPlayerTurn() {
		AbstractPlayer p = AbstractDungeon.player;
	    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new FocusPower(p, -1), -1));
	    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, -1), -1));
		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new DexterityPower(p, -1), -1));
	    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new WeakPower(p, 3, false), 3));
	    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new FrailPower(p, 3, false), 3));
	    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new VulnerablePower(p, 3, false), 3));
	}
    
    public AbstractCard makeCopy() {
		if (Sins.isObtained())
	        return new Lust();
		return Sins.copyCurse();
    }//复制卡牌后复制的卡，如果卡组里有复制卡牌的卡每张卡都要有这个

    public void upgrade() {
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}