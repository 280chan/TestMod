
package cards.curse;

import basemod.abstracts.*;
import mymod.TestMod;
import relics.Prudence;
import relics.Sins;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.AngryPower;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;

public class Wrath extends CustomCard {
    public static final String ID = "Wrath";
    public static final String NAME = "暴怒";
    public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = " 不能被打出 。抽到这张牌时，给所有敌人增加1层 生气 。在手牌时，只能打出 攻击 牌。 虚无 。";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
    private static final int COST = -2;//卡牌费用

    public Wrath() {
    	super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.CURSE, CardColor.CURSE, CardRarity.SPECIAL, CardTarget.NONE);
        this.isEthereal = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
	}
	
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
    	return p.hasRelic(Prudence.ID) || p.hasRelic("Blue Candle");
	}

	public boolean canPlay(AbstractCard card) {
		if (AbstractDungeon.player.hasRelic(Prudence.ID))
			return true;
		if (card.type == CardType.ATTACK)
			return true;
		card.cantUseMessage = "暴怒:我无法打出 #r非攻击牌 ";
		return false;
	}
	
	public AbstractCard makeCopy() {
		if (Sins.isObtained())
			return new Wrath();
		return Sins.copyCurse();
	}

	public void triggerWhenDrawn() {
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!m.isDead && !m.isDying) {
				AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, AbstractDungeon.player, new AngryPower(m, 1), 1));
			}
		}
	}

    public void upgrade() {
    }//升级后额外增加（括号内的）值，以及升级后的各种改变
}