package relics;

import java.util.stream.Stream;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

public class Dye extends AbstractTestRelic implements ClickableRelic {
	public static final String ID = "Dye";
	private boolean used = false;
	
	public Dye() {
		super(ID, RelicTier.RARE, LandingSound.MAGICAL);
		this.setTestTier(GOD);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atPreBattle() {
		this.used = false;
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
	}
	
	@Override
	public void onRightClick() {
		AbstractPlayer p = AbstractDungeon.player;
		if (AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT && !this.used
				&& !(p.hand.isEmpty() || (p.drawPile.isEmpty() && p.discardPile.isEmpty()))) {
			this.used = true;
			this.addTmpActionToTop(() -> {
				AbstractDungeon.handCardSelectScreen.open("选择", 1, false, false, false, false);
				this.addTmpActionToTop(() -> {
					AbstractCard c = AbstractDungeon.handCardSelectScreen.selectedCards.getTopCard();
					Stream.of(p.drawPile, p.discardPile).forEach(g -> changeCards(g, c));
					AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
					p.hand.addToTop(c);
					p.hand.refreshHandLayout();
				});
			});
		}
	}
	
	private void changeCards(CardGroup g, AbstractCard c) {
		g.group = g.group.stream().map(a -> c.makeStatEquivalentCopy()).collect(this.collectToArrayList());
	}

}