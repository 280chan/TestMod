package actions;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import utils.MiscMethods;

public class CardIndexAction extends AbstractGameAction implements MiscMethods {
	private static final float DURATION = Settings.ACTION_DUR_FAST;
	private ArrayList<AbstractCard> cards;
	private AbstractCard c;
	
	public CardIndexAction(AbstractCard thisCard, AbstractMonster m, ArrayList<AbstractCard> cards) {
		this.actionType = ActionType.USE;
		this.duration = DURATION;
		this.target = m;
		this.c = thisCard;
		this.cards = cards;
		this.amount = this.c.magicNumber;
	}

	@Override
	public void update() {
		if (cards.isEmpty()) {
    		this.addToTop(new CardIndexInitializeAction(this.c, this.amount, this.cards));
    	} else if (cards.size() == 1 && cards.get(0) == null) {

		} else {
			for (int i = 0; i < cards.size(); i++) {
    			AbstractCard c = cards.get(i);
    			c.applyPowers();
    			if (c.cost == -1)
    				c.energyOnUse = EnergyPanel.totalCount;
    			this.playAgain(c, (AbstractMonster)this.target);
    		}
    	}
		this.isDone = true;
	}

}
