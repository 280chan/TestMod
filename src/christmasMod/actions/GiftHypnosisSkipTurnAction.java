package christmasMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;

import utils.MiscMethods;

public class GiftHypnosisSkipTurnAction extends AbstractGameAction implements MiscMethods {
	private static final float DURATION = Settings.ACTION_DUR_XFAST;
	
	private AbstractCard c;
	
	public GiftHypnosisSkipTurnAction(AbstractCard c) {
		this.actionType = ActionType.SPECIAL;
		this.duration = DURATION;
		this.c = c;
	}

	@Override
	public void update() {
		this.isDone = true;
		this.turnSkipperStartByCard(c);
	}

}
