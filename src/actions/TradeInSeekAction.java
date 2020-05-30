package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class TradeInSeekAction extends AbstractGameAction {
	public static final float DURATION = Settings.ACTION_DUR_MED;
	private AbstractPlayer p;
	private int costDown;
	
	public TradeInSeekAction(AbstractPlayer p, int amount, int costDown) {
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.duration = DURATION;
		this.amount = amount;
		this.costDown = costDown;
		this.p = p;
	}

	private void setCard(AbstractCard c) {
		c.exhaustOnUseOnce = true;
		c.setCostForTurn(c.costForTurn - this.costDown);
	}
	
	@Override
	public void update() {
		CardGroup tmp;
		if (this.duration == DURATION) {
			if (this.p.drawPile.isEmpty()) {
				this.isDone = true;
				return;
			}
			tmp = new CardGroup(CardGroupType.UNSPECIFIED);
			for (AbstractCard c : this.p.drawPile.group) {
				tmp.addToRandomSpot(c);
			}
			if (tmp.size() <= this.amount) {
				for (int i = 0; i < tmp.size(); i++) {
					AbstractCard c = tmp.getNCardFromTop(i);
					this.setCard(c);
					if (this.p.hand.size() == 10) {
						this.p.drawPile.moveToDiscardPile(c);
						this.p.createHandIsFullDialog();
					} else {
						c.unhover();
						c.lighten(true);
						c.setAngle(0.0F);
						c.drawScale = 0.12F;
						c.targetDrawScale = 0.75F;
						c.current_x = CardGroup.DRAW_PILE_X;
						c.current_y = CardGroup.DRAW_PILE_Y;
						this.p.drawPile.removeCard(c);
						this.p.hand.addToTop(c);
						this.p.hand.refreshHandLayout();
						this.p.hand.applyPowers();
					}
				}
				this.isDone = true;
				return;
			}
			AbstractDungeon.gridSelectScreen.open(tmp, this.amount, "选择" + this.amount + "张牌加入手牌并降低" + this.costDown + "耗能", false);
			tickDuration();
			return;
		}
		if (AbstractDungeon.gridSelectScreen.selectedCards.size() != 0) {
			for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
				c.unhover();
				this.setCard(c);
				if (this.p.hand.size() == 10) {
					this.p.drawPile.moveToDiscardPile(c);
					this.p.createHandIsFullDialog();
				} else {
					this.p.drawPile.removeCard(c);
					this.p.hand.addToTop(c);
				}
				this.p.hand.refreshHandLayout();
				this.p.hand.applyPowers();
			}
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
			this.p.hand.refreshHandLayout();
		}
		tickDuration();
	}
	
}
