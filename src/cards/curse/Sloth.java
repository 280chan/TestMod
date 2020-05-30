
package cards.curse;

import basemod.abstracts.*;
import mymod.TestMod;
import relics.Prudence;
import relics.Sins;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;

public class Sloth extends CustomCard {
    public static final String ID = "Sloth";
    public static final String NAME = "怠惰";
    public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = " 不能被打出 。第二回合起，在手牌时，每回合出牌数量不能超过本场战斗最少的单回合出牌数。这个 诅咒 在第一回合无法逃脱。";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
    private static final int COST = -2;//卡牌费用 
    public static final String[] EXTENDED_DESCRIPTION = {" 不能被打出 。", "在手牌时，出牌数量不能超过", "。你已经出了", "张牌。", "这个 诅咒 现在无法逃脱。"};
    
    private static int minNumCardsPlayed = -1;
    
    public int getMinimum() {
    	return minNumCardsPlayed;
    }
    
    public static void endTurn() {
    	if (minNumCardsPlayed == -1 || !checkMinimum()) {
        	minNumCardsPlayed = AbstractDungeon.player.cardsPlayedThisTurn;
    	}
    }
    
    public static void startBattle() {
    	minNumCardsPlayed = -1;
    }
    
    public Sloth() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.CURSE, CardColor.CURSE, CardRarity.SPECIAL, CardTarget.NONE);
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
	}
    
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
    	return p.hasRelic(Prudence.ID) || p.hasRelic("Blue Candle");
	}

	public boolean canPlay(AbstractCard card) {
		if (AbstractDungeon.player.hasRelic(Prudence.ID))
			return true;
		if (checkMinimum() && minNumCardsPlayed > -1) {
			card.cantUseMessage = "怠惰:我无法打出 #r" + minNumCardsPlayed + " 张以上的牌";
			return false;
		}
		return true;
	}

	private static boolean checkMinimum() {
		return AbstractDungeon.player.cardsPlayedThisTurn >= minNumCardsPlayed;
	}

	public void triggerOnExhaust() {
		if (minNumCardsPlayed == -1) {
			AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(makeCopy()));
		}
	}

	public void applyPowers() {
		super.applyPowers();
		if (minNumCardsPlayed == -1) {
			this.rawDescription = (EXTENDED_DESCRIPTION[0] + EXTENDED_DESCRIPTION[4]);
		} else {
			this.rawDescription = (EXTENDED_DESCRIPTION[0] + EXTENDED_DESCRIPTION[1] + minNumCardsPlayed
					+ EXTENDED_DESCRIPTION[2] + AbstractDungeon.player.cardsPlayedThisTurn + EXTENDED_DESCRIPTION[3]);
		}
		initializeDescription();
	}

	public AbstractCard makeCopy() {
		if (Sins.isObtained())
			return new Sloth();
		return Sins.copyCurse();
	}// 复制卡牌后复制的卡，如果卡组里有复制卡牌的卡每张卡都要有这个

	public void upgrade() {
	}// 升级后额外增加（括号内的）值，以及升级后的各种改变
}