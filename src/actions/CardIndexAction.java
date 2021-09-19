package actions;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import utils.MiscMethods;

public class CardIndexAction extends AbstractGameAction implements MiscMethods {
	private static final float DURATION = Settings.ACTION_DUR_FAST;
	private ArrayList<AbstractCard> cards;
	private AbstractCard c;
	private AbstractMonster m;
	
	public CardIndexAction(AbstractCard thisCard, AbstractMonster m, ArrayList<AbstractCard> cards) {
		this.actionType = ActionType.USE;
		this.duration = DURATION;
		this.m = m;
		this.c = thisCard;
		this.cards = cards;
		this.amount = this.c.magicNumber;
	}
	
	@Override
	public void update() {
		if (this.cards.isEmpty()) {
    		this.addToTop(new CardIndexInitializeAction(this.c, this.amount, this.cards));
    	} else if (this.cards.size() == 1 && this.cards.get(0) == null) {

		} else {
			this.cards.stream().forEach(this::setXCostEnergy);
			this.autoplayInOrder(this.c, this.cards, this.m);
    	}
		this.isDone = true;
	}

}
