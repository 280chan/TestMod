package testmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import testmod.mymod.TestMod;
import testmod.utils.MiscMethods;

public class WormholeAction extends AbstractGameAction implements MiscMethods {
	private static final UIStrings UI = MISC.uiString();
	private float startingDuration;
	private CardGroup g;
	private boolean exhaust;
	private AbstractCreature t;

	public WormholeAction(CardGroup g, AbstractCreature t, boolean exhaust) {
		this.actionType = ActionType.SPECIAL;
		this.startingDuration = Settings.ACTION_DUR_MED;
		this.duration = this.startingDuration;
		this.g = g;
		this.exhaust = exhaust;
		this.t = t;
	}

	@Override
	public void update() {
		if (this.duration == this.startingDuration) {
			TestMod.info("虫洞:开始判定可选卡牌");
			switch (g.group.size()) {
			case 1:
				this.addToTop(new PlaySpecificCardAction((AbstractMonster) t, g.getTopCard(),
						this.getSource(g.getTopCard()), checkExhaust(g.getTopCard())));
			case 0:
				this.isDone = true;
				return;
			}
			AbstractDungeon.gridSelectScreen.open(g, 1, UI.TEXT[exhaust ? 1 : 0], false, false, false, false);
		} else if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
			AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
			this.addToTop(new PlaySpecificCardAction((AbstractMonster)t, c, this.getSource(c), checkExhaust(c)));
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
			this.isDone = true;
		}
		tickDuration();
	}

	private boolean checkExhaust(AbstractCard c) {
		return c.type == CardType.STATUS || c.type == CardType.CURSE || this.exhaust;
	}
	
}
