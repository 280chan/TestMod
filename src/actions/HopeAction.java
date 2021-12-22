package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

import utils.MiscMethods;

public class HopeAction extends AbstractGameAction implements MiscMethods {
	private float startingDuration;
	public static final int MAX_NUM = 10;
	private static final UIStrings UI = INSTANCE.uiString();

	public HopeAction() {
		this.amount = MAX_NUM;
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.startingDuration = Settings.ACTION_DUR_FAST;
		this.duration = this.startingDuration;
	}

	@Override
	public void update() {
		if (this.duration == this.startingDuration) {
			if (AbstractDungeon.player.masterDeck.isEmpty()) {
				this.isDone = true;
				return;
			}
			CardGroup tmpGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
			tmpGroup.group = AbstractDungeon.player.masterDeck.group.stream().collect(this.toArrayList());
			AbstractDungeon.gridSelectScreen.open(tmpGroup, this.amount, true, UI.TEXT[0]);
		} else if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
			AbstractDungeon.gridSelectScreen.selectedCards.stream().map(AbstractCard::makeStatEquivalentCopy)
					.map(MakeTempCardInHandAction::new).forEach(this::addToTop);
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
		}
		tickDuration();
	}

}
