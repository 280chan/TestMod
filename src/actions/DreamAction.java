package actions;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.WhiteNoise;
import com.megacrit.cardcrawl.cards.colorless.Discovery;
import com.megacrit.cardcrawl.cards.green.Distraction;
import com.megacrit.cardcrawl.cards.red.InfernalBlade;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class DreamAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_FAST;
	private static final AbstractCard[] CARDS = {new InfernalBlade(), new Distraction(), new WhiteNoise(), new Discovery()};

	public DreamAction() {
		this.duration = DURATION;
		this.actionType = ActionType.CARD_MANIPULATION;
	}

	private ArrayList<AbstractCard> cards() {
		ArrayList<AbstractCard> derp = new ArrayList<AbstractCard>();
		AbstractCard tmp;
		for (int i = 0; i < CARDS.length; i++) {
			tmp = CARDS[i].makeCopy();
			tmp.costForTurn = 0;
			derp.add(tmp);
			UnlockTracker.markCardAsSeen(tmp.cardID);
		}
		return derp;
	}

	@Override
	public void update() {
		if (this.duration == DURATION) {
			for (AbstractCard c : cards()) {
		    	AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(c));
			}
			this.isDone = true;
			return;
		}
	}

}
