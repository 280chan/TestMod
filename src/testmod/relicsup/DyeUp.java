package testmod.relicsup;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import testmod.relics.Dye;

public class DyeUp extends AbstractUpgradedRelic implements ClickableRelic {
	private static final UIStrings UI = Dye.UI;
	private boolean used = false;
	private static final ArrayList<AbstractCard> COPY = new ArrayList<AbstractCard>();
	
	public void onUseCard(final AbstractCard c, final UseCardAction action) {
		if (this.isActive && COPY.contains(c)) {
			p().gainGold((int) this.relicStream(DyeUp.class).count());
		}
	}
	
	public void atPreBattle() {
		this.used = false;
		COPY.clear();
		this.togglePulse(true);
    }
	
	private void togglePulse(boolean flag) {
		if (flag && !this.used) {
			this.beginLongPulse();
			this.counter = -2;
		} else {
			this.stopPulse();
			this.counter = -1;
		}
	}
	
	public void atTurnStart() {
		this.togglePulse(true);
	}
	
	public void onPlayerEndTurn() {
		this.togglePulse(false);
    }
	
	public void onVictory() {
		this.togglePulse(false);
		COPY.clear();
	}
	
	@Override
	public void onRightClick() {
		if (AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT && !this.used
				&& !(p().hand.isEmpty() || (p().drawPile.isEmpty() && p().discardPile.isEmpty()))) {
			this.used = true;
			this.togglePulse(false);
			this.addTmpActionToTop(() -> {
				AbstractDungeon.handCardSelectScreen.open(UI.TEXT[0], 1, false, false, false, false);
				this.addTmpActionToTop(() -> {
					AbstractCard c = AbstractDungeon.handCardSelectScreen.selectedCards.getTopCard();
					Stream.of(p().drawPile, p().discardPile).forEach(g -> changeCards(g, c));
					AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
					p().hand.addToTop(c);
					p().hand.refreshHandLayout();
				});
			});
		}
	}
	
	private void changeCards(CardGroup g, AbstractCard c) {
		ArrayList<AbstractCard> old = g.group;
		g.group = g.group.stream().map(a -> c.makeStatEquivalentCopy()).collect(this.toArrayList());
		COPY.addAll(g.group);
		old.clear();
	}

}