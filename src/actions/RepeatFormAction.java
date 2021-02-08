package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import mymod.TestMod;
import powers.RepeatFormPower;

public class RepeatFormAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_FAST;
	private CardGroup g;
	private AbstractPlayer p;

	public RepeatFormAction(AbstractPlayer p, CardGroup g) {
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.duration = DURATION;
		this.g = g;
		this.p = p;
	}
	
	public RepeatFormAction(AbstractPlayer p, AbstractCard thisCard) {
		this(p, createGroup(thisCard));
		this.amount = thisCard.magicNumber;
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
			String info = "选择1张牌复读形态。(排列：抽牌堆、手牌、弃牌堆)";
			AbstractDungeon.gridSelectScreen.open(g, 1, info, false, false, false, false);
		} else if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
			this.addPowerToPlayer(AbstractDungeon.gridSelectScreen.selectedCards.get(0));
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
			this.isDone = true;
		}
		tickDuration();
	}

	private CardGroup getSource(AbstractCard c) {
		CardGroup[] groups = {this.p.discardPile, this.p.drawPile, this.p.hand};
		for (CardGroup g : groups)
			if (g.contains(c)) {
				TestMod.info("来自于" + g.type);
				return g;
			}
		TestMod.info("为什么找不到" + c.name + "？？？");
		return null;
	}
	
	private RepeatFormPower createPower(AbstractCard c) {
		return new RepeatFormPower(p, this.amount, c);
	}
	
	private void addPowerToPlayer(AbstractCard c) {
		this.getSource(c).removeCard(c);
		this.addToTop(new ApplyPowerAction(p, p, createPower(c), this.amount));
	}
	
}
