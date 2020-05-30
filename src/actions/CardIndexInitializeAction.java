package actions;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

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
			if (g.isEmpty()) {
				this.list.add(null);
				this.isDone = true;
				return;
			}
			int size = Math.min(this.amount, g.size());
			String info = "选择" + size + "张牌卡牌索引";
			AbstractDungeon.gridSelectScreen.open(g, size, true, info);
			//AbstractDungeon.gridSelectScreen.open(g, this.amount, info, false, false, false, false);
		} else if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
			System.out.print("选择了");
			for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
				System.out.print(c.name + " ");
				this.removeCard(c);
			}
			System.out.println();
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
			this.isDone = true;
		} else {
			System.out.println("取消了选择，之后打出不会生效。");
			this.list.add(null);
			this.isDone = true;
		}
		tickDuration();
	}

	private static CardGroup getSource(AbstractCard c) {
		AbstractPlayer p = AbstractDungeon.player;
		CardGroup[] groups = {p.discardPile, p.drawPile, p.hand};
		for (CardGroup g : groups)
			if (g.contains(c)) {
				System.out.println("来自于" + g.type);
				return g;
			}
		System.out.println("为什么找不到" + c.name + "？？？");
		return null;
	}
	
	private void removeCard(AbstractCard c) {
		getSource(c).removeCard(c);
		this.list.add(c);
	}
	
}
