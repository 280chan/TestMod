package actions;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import powers.TemporaryDeletionPower;

public class TemporaryDeletionAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_FAST;
	private CardGroup g;

	public TemporaryDeletionAction(CardGroup g, AbstractCreature target) {
		this.actionType = ActionType.CARD_MANIPULATION;
		this.duration = DURATION;
		this.g = g;
		this.target = target;
	}

	@Override
	public void update() {
		if (this.duration == DURATION) {
			if (g.group.size() == 1) {
				deleteCard(g.getTopCard());
				this.isDone = true;
				return;
			} else if (g.group.isEmpty()) {
				this.isDone = true;
				return;
			}
			String info = "[临时删除]";
			AbstractDungeon.gridSelectScreen.open(g, 1, info, false, false, false, false);
		} else if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
			deleteCard(AbstractDungeon.gridSelectScreen.selectedCards.get(0));
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
			this.isDone = true;
		}
		tickDuration();
	}
	
	private void deleteCard(AbstractCard c) {
		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this.target, this.target, new TemporaryDeletionPower(target, 1, c), 1));
		deleteType(c.rarity);
	}
	
	private static void deleteType(CardRarity rarity) {
		AbstractPlayer p = AbstractDungeon.player;
		ArrayList<AbstractCard> list = new ArrayList<AbstractCard>();
		for (AbstractCard c : p.drawPile.group) {
			if (c.rarity == rarity) {
				list.add(c);
			}
		}
		for (AbstractCard c : p.hand.group) {
			if (c.rarity == rarity) {
				list.add(c);
			}
		}
		for (AbstractCard c : p.discardPile.group) {
			if (c.rarity == rarity) {
				list.add(c);
			}
		}
		deleteCards(list);
	}
	
	private static void deleteCards(ArrayList<AbstractCard> list) {
		AbstractPlayer p = AbstractDungeon.player;
		for (AbstractCard c : list)
			if (p.drawPile.contains(c))
				p.drawPile.removeCard(c);
		for (AbstractCard c : list)
			if (p.hand.contains(c))
				p.hand.removeCard(c);
		for (AbstractCard c : list)
			if (p.discardPile.contains(c))
				p.discardPile.removeCard(c);
	}
	
}
