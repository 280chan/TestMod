package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class HopeAction extends AbstractGameAction {

	private float startingDuration;
	public static final int MAX_NUM = 10;

	public HopeAction() {
		this.amount = MAX_NUM;
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.startingDuration = Settings.ACTION_DUR_FAST;
		this.duration = this.startingDuration;
	}

	@Override
	public void update() {
		CardGroup tmpGroup;
		if (this.duration == this.startingDuration) {
			if (AbstractDungeon.player.masterDeck.isEmpty()) {
				this.isDone = true;
				return;
			}
			tmpGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
			for (int i = 0; i < AbstractDungeon.player.masterDeck.size(); i++) {
				tmpGroup.addToTop((AbstractCard) AbstractDungeon.player.masterDeck.group
						.get(AbstractDungeon.player.masterDeck.size() - i - 1));
			}
			AbstractDungeon.gridSelectScreen.open(tmpGroup, this.amount, true, "选择最多10张牌加入手牌");
		} else if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
			for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
				AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(c.makeStatEquivalentCopy()));
			}
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
		}
		tickDuration();
	}

}
