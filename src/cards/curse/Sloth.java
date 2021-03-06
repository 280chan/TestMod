
package cards.curse;

import cards.AbstractTestCurseCard;
import relics.Sins;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;

public class Sloth extends AbstractTestCurseCard {
    public static final String ID = "Sloth";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    
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
        super(ID, NAME, DESCRIPTION);
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
	}
}