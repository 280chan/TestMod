package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import powers.ReproducePower;

public class ReproduceAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_FAST;
	private CardGroup g;
	private AbstractPlayer p;

	public ReproduceAction(AbstractPlayer p, CardGroup g) {
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.duration = DURATION;
		this.g = g;
		this.p = p;
	}
	
	public ReproduceAction(AbstractCard thisCard, int magicNumber) {
		this(AbstractDungeon.player, createGroup(thisCard));
		this.amount = magicNumber;
	}

	private static CardGroup createGroup(AbstractCard c) {
		CardGroup tmp = new CardGroup(CardGroupType.UNSPECIFIED);
		AbstractPlayer p = AbstractDungeon.player;
		tmp.group.addAll(p.drawPile.group);
		tmp.group.addAll(p.hand.group);
		tmp.group.addAll(p.discardPile.group);
		tmp.removeCard(c);
		for (AbstractCard t : p.hand.group)
        	t.beginGlowing();
		return tmp;
	}
	
	@Override
	public void update() {
		if (this.duration == DURATION) {
			switch (g.group.size()) {
			case 1:
				this.addPowerToPlayer(g.getTopCard());
			case 0:
				this.isDone = true;
				return;
			}
			String info = "选择1张牌复刻。(排列：抽牌堆、手牌、弃牌堆)";
			AbstractDungeon.gridSelectScreen.open(g, 1, info, false, false, false, false);
		} else if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
			AbstractCard tmp = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
			tmp.returnToHand = true;
			tmp.retain = true;
			this.addPowerToPlayer(tmp);
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
			this.isDone = true;
		}
		tickDuration();
	}
	
	private void addPowerToPlayer(AbstractCard c) {
		this.addToTop(new ApplyPowerAction(p, p, new ReproducePower(p, c, this.amount), this.amount));
	}
	
}
