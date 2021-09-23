package actions;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import mymod.TestMod;

public class CardIndexInitializeAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_FAST;
	private ArrayList<AbstractCard> list;
	private CardGroup g;
	
	public CardIndexInitializeAction(AbstractCard thisCard, int amount, ArrayList<AbstractCard> list) {
		this.actionType = ActionType.SPECIAL;
		this.duration = DURATION;
		this.g = createGroup(thisCard);
		this.list = list;
		this.amount = amount;
	}

	private static CardGroup createGroup(AbstractCard c) {
		CardGroup tmp = new CardGroup(CardGroupType.UNSPECIFIED);
		AbstractPlayer p = AbstractDungeon.player;
		Stream.of(p.discardPile, p.hand, p.drawPile).flatMap(g -> g.group.stream()).forEach(tmp.group::add);
		tmp.removeCard(c);
		p.hand.group.forEach(AbstractCard::beginGlowing);
		return tmp;
	}
	
	@Override
	public void update() {
		this.isDone = true;
		if (this.duration == DURATION) {
			if (g.isEmpty()) {
				this.list.add(null);
				return;
			}
			this.isDone = false;
			int size = Math.min(this.amount, g.size());
			String info = "选择" + size + "张牌卡牌索引";
			AbstractDungeon.gridSelectScreen.open(g, size, true, info);
			//AbstractDungeon.gridSelectScreen.open(g, this.amount, info, false, false, false, false);
		} else if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
			String tmp = "选择了" + AbstractDungeon.gridSelectScreen.selectedCards.stream().sequential()
					.peek(this::removeCard).map(c -> c.name + " ").reduce("", (a, b) -> a + b);
			TestMod.info(tmp);
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
		} else {
			TestMod.info("取消了选择，之后打出不会生效。");
			this.list.add(null);
		}
		tickDuration();
	}
	
	private static CardGroup getSource(AbstractCard c) {
		AbstractPlayer p = AbstractDungeon.player;
		return Stream.of(p.discardPile, p.hand, p.drawPile).filter(g -> g.contains(c)).findAny().orElse(null);
	}
	
	private void removeCard(AbstractCard c) {
		getSource(c).removeCard(c);
		this.list.add(c);
	}
	
}
