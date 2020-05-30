package actions;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;

import relics.MyRelic;

public class MagicalMalletAction extends AbstractGameAction {

	private static final float DURATION = Settings.ACTION_DUR_XFAST;

	private MyRelic r;
	private ArrayList<AbstractCard> hand;
	
	public MagicalMalletAction(MyRelic r, ArrayList<AbstractCard> hand) {
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.duration = DURATION;
		this.hand = hand;
		this.r = r;
	}

	@Override
	public void update() {
		this.isDone = true;
		int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
		for (AbstractCard c : this.hand) {
			System.out.println(c.name + c.cost + "," + c.costForTurn);
			if (c.cost < 0)
				continue;
			if (min > c.costForTurn)
				min = c.costForTurn;
			if (max < c.costForTurn)
				max = c.costForTurn;
		}
		System.out.println("最大: " + max + ",最小: " + min);
		if (min == max)
			return;
		if (min == Integer.MAX_VALUE && max == Integer.MIN_VALUE)
			return;
		for (AbstractCard c : this.hand) {
			if (c.costForTurn == min) {
				c.modifyCostForCombat(max - c.costForTurn);
				c.costForTurn = c.cost;
			} else if (c.costForTurn == max) {
				c.modifyCostForCombat(min - c.costForTurn);
				c.costForTurn = c.cost;
			}
		}
		r.show();
	}

}
