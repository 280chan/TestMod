package actions;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ArrangementUpgradingAction extends AbstractGameAction {
	public static final float DURATION = Settings.ACTION_DUR_FAST;
	private ArrayList<AbstractCard> cannotUpgrade = new ArrayList<AbstractCard>();
	AbstractPlayer p;
	
	public ArrangementUpgradingAction(AbstractPlayer p, int x) {
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.duration = DURATION;
		this.amount = x;
		this.p = p;
	}

	@Override
	public void update() {
		if (this.duration == DURATION) {
			if (this.amount < 1) {
				this.isDone = true;
				return;
			}
			for (AbstractCard c : this.p.hand.group) {
		        if (!c.canUpgrade()) {
		        	this.cannotUpgrade.add(c);
		        }
		    }
			if (this.cannotUpgrade.size() == this.p.hand.size()) {
				this.gainEnergy(this.amount);
				this.isDone = true;
				return;
			}
			this.p.hand.group.removeAll(this.cannotUpgrade);
			AbstractDungeon.handCardSelectScreen.open("安排(最多" + this.amount + "张)", this.amount, true, true, false, true);
			tickDuration();
			return;
		}
		if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
			this.gainEnergy(this.amount - AbstractDungeon.handCardSelectScreen.selectedCards.size());
			for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
				c.upgrade();
				c.superFlash();
				this.p.hand.addToTop(c);
			}
			returnCards();
			AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
			AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
			this.isDone = true;
		}
		tickDuration();
	}
	
	private void gainEnergy(int amount) {
		AbstractDungeon.actionManager.addToTop(new GainEnergyAction(amount));
	}

	private void returnCards() {
		for (AbstractCard c : this.cannotUpgrade) {
			this.p.hand.addToTop(c);
		}
		this.p.hand.refreshHandLayout();
	}
	
}
