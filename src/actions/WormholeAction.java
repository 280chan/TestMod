package actions;

import java.util.stream.Stream;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import mymod.TestMod;

public class WormholeAction extends AbstractGameAction {
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
				this.addToTop(new PlaySpecificCardAction((AbstractMonster)t, g.getTopCard(), getSource(g.getTopCard()), checkExhaust(g.getTopCard())));
			case 0:
				this.isDone = true;
				return;
			}
			String info = "选择1张牌打出";
			if (exhaust)
				info += "并消耗";
			info += "。(排列：抽牌堆、手牌、弃牌堆)";
			AbstractDungeon.gridSelectScreen.open(g, 1, info, false, false, false, false);
		} else if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
			AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
			this.addToTop(new PlaySpecificCardAction((AbstractMonster)t, c, getSource(c), checkExhaust(c)));
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
			this.isDone = true;
		}
		tickDuration();
	}

	private boolean checkExhaust(AbstractCard c) {
		return c.type == CardType.STATUS || c.type == CardType.CURSE || this.exhaust;
	}
	
	private static CardGroup getSource(AbstractCard c) {
		AbstractPlayer p = AbstractDungeon.player;
		return Stream.of(p.discardPile, p.hand, p.drawPile).filter(g -> g.contains(c)).findAny().orElse(null);
	}
	
}
