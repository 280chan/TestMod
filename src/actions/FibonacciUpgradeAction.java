package actions;

import java.util.UUID;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;

public class FibonacciUpgradeAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_XFAST;
	private UUID uuid;
	
	public FibonacciUpgradeAction(UUID uuid) {
		this.duration = DURATION;
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.uuid = uuid;
	}
	
	public void update() {
		for (AbstractCard c : GetAllInBattleInstances.get(this.uuid)) {
			c.upgrade();
		}
	    this.isDone = true;
	}
}
