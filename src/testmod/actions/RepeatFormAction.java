package testmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

import testmod.powers.RepeatFormPower;
import testmod.utils.MiscMethods;

public class RepeatFormAction extends AbstractGameAction implements MiscMethods {
	private static final UIStrings UI = MISC.uiString();
	private static final float DURATION = Settings.ACTION_DUR_FAST;
	private CardGroup g;

	public RepeatFormAction(CardGroup g) {
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.duration = DURATION;
		this.g = g;
	}
	
	public RepeatFormAction(AbstractCard thisCard) {
		this(createGroup(thisCard));
		this.amount = thisCard.magicNumber;
	}

	private static CardGroup createGroup(AbstractCard c) {
		CardGroup tmp = new CardGroup(CardGroupType.UNSPECIFIED);
		MISC.combatCards().forEach(tmp.group::add);
		tmp.removeCard(c);
		MISC.p().hand.group.forEach(AbstractCard::beginGlowing);
		return tmp;
	}
	
	@Override
	public void update() {
		if (this.amount < 1) {
			this.isDone = true;
			return;
		}
		if (this.duration == DURATION) {
			switch (g.group.size()) {
			case 1:
				this.addPowerToPlayer(g.getTopCard());
			case 0:
				this.isDone = true;
				return;
			}
			AbstractDungeon.gridSelectScreen.open(g, 1, UI.TEXT[0], false, false, false, false);
		} else if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
			this.addPowerToPlayer(AbstractDungeon.gridSelectScreen.selectedCards.get(0));
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
			this.isDone = true;
		}
		tickDuration();
	}
	
	private void addPowerToPlayer(AbstractCard c) {
		this.getSource(c).removeCard(c);
		this.addToTop(apply(p(), new RepeatFormPower(p(), this.amount, c)));
	}
	
}
