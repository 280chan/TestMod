package actions;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class TradeInAction extends AbstractGameAction {
	public static final float DURATION = Settings.ACTION_DUR_FAST;
	AbstractPlayer p;
	
	public TradeInAction(AbstractPlayer p, int magicNumber) {
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.duration = DURATION;
		this.amount = magicNumber;
		this.p = p;
	}

	@Override
	public void update() {
		if (this.duration == DURATION) {
			if (p.hand.isEmpty()) {
				this.isDone = true;
				return;
			}
			AbstractDungeon.handCardSelectScreen.open("消耗", p.hand.size(), true, true, false, false);
			tickDuration();
			return;
		}
		if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
			int size = AbstractDungeon.handCardSelectScreen.selectedCards.size();
			if (size > 0)
				AbstractDungeon.actionManager.addToBottom(new TradeInSeekAction(p, size, this.amount));
			exhaustsCards(AbstractDungeon.handCardSelectScreen.selectedCards.group);
			AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
			AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
			this.isDone = true;
		}
		tickDuration();
	}

	private void exhaustsCards(ArrayList<AbstractCard> list) {
		for (AbstractCard c : list) {
			this.p.hand.moveToExhaustPile(c);
			CardCrawlGame.dungeon.checkForPactAchievement();
			c.exhaustOnUseOnce = false;
			c.freeToPlayOnce = false;
		}
		this.p.hand.refreshHandLayout();
	}
	
}
