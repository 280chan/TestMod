
package cards.curse;

import basemod.abstracts.*;
import mymod.TestMod;
import relics.Sins;
import utils.MiscMethods;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;

public class Sloth extends CustomCard implements MiscMethods {
    public static final String ID = "Sloth";
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(TestMod.makeID(ID));
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    public static final String IMG = TestMod.cardIMGPath("relic1");
    private static final int COST = -2;//卡牌费用 
    
    private static int minNumCardsPlayed = -1;
    
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
    	return this.hasPrudence() || p.hasRelic("Blue Candle");
	}

	public boolean canPlay(AbstractCard card) {
		if (this.hasPrudence())
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