package actions;

import java.util.Iterator;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class SubstituteBySubterfugeAction extends AbstractGameAction {

	private float startingDuration;
	private AbstractPlayer p;
	private boolean shuffled;
	private boolean vfxDone;
	private int count;
	private boolean discarded;

	public SubstituteBySubterfugeAction(AbstractPlayer p) {
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.startingDuration = Settings.ACTION_DUR_FAST;
		this.duration = this.startingDuration;
		this.p = p;
		this.amount = p.drawPile.size();
	}

	@Override
	public void update() {
		CardGroup tmpGroup;
		if (this.duration == this.startingDuration) {
			if (!p.drawPile.isEmpty()) {
				tmpGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
				for (int i = 0; i < this.amount; i++) {
					tmpGroup.addToTop(p.drawPile.group.get(this.amount - i - 1));
				}
				AbstractDungeon.gridSelectScreen.open(tmpGroup, this.amount, true, "选择丢弃任意张牌");
			}
		} else {
			if (!this.shuffled) {
				for (AbstractRelic r : p.relics) {
					r.onShuffle();
				}
				this.shuffled = true;
				p.discardPile.shuffle(AbstractDungeon.shuffleRng);
			}
			if (!this.vfxDone) {
				Iterator<AbstractCard> i = p.discardPile.group.iterator();
				if (i.hasNext()) {
					this.count += 1;
					AbstractCard e = i.next();
					i.remove();
					if (this.count < 11) {
						AbstractDungeon.getCurrRoom().souls.shuffle(e, false);
					} else {
						AbstractDungeon.getCurrRoom().souls.shuffle(e, true);
					}
					return;
				}
				this.vfxDone = true;
			}

			if (!this.discarded) {
				this.discarded = true;
				for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
					p.drawPile.moveToDiscardPile(c);
				}
				AbstractDungeon.gridSelectScreen.selectedCards.clear();
			}
		}
		tickDuration();
	}

}
