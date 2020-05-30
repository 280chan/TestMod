package deprecated.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import deprecated.powers.StasisPower;

/**
 * @deprecated
 */
public class StasisFormAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_FAST;
	private boolean upgraded;
	private AbstractMonster t;
	private static final String[] TEXT = {"变为0费并", "将其复制品凝滞"};

	public StasisFormAction(int amount, AbstractMonster t, boolean upgraded) {
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.duration = DURATION;
		this.upgraded = upgraded;
		this.amount = amount;
		this.t = t;
	}
	
	public StasisFormAction(AbstractMonster t, boolean upgraded) {
		this(1, t, upgraded);
	}

	@Override
	public void update() {
		AbstractPlayer p = AbstractDungeon.player;
		if (this.duration == DURATION) {
			String info = "";
			if (this.upgraded)
				info += TEXT[0];
			info += TEXT[1];
	        if (p.hand.size() > this.amount) {
	        	AbstractDungeon.handCardSelectScreen.open(info, this.amount, false);
	        } else {
				this.apply(p, p.hand);
				this.isDone = true;
	        }
	        tickDuration();
	        return;
		} else if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
			this.apply(p, AbstractDungeon.handCardSelectScreen.selectedCards);
			AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
			this.isDone = true;
		}
		tickDuration();
	}
	
	private void apply(AbstractPlayer p, CardGroup group) {
		for (AbstractCard c : group.group) {
			if (this.upgraded && c.cost > 0)
				c.modifyCostForCombat(-1);
			AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this.t, p, new StasisPower(this.t, c.makeStatEquivalentCopy())));
			if (group.type != CardGroupType.HAND)
				p.hand.addToTop(c);
		}
	}

}
