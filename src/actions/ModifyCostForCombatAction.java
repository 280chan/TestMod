package actions;

import java.util.UUID;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;

public class ModifyCostForCombatAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_XFAST;
	private UUID uuid;
	
	public ModifyCostForCombatAction(UUID uuid, int amount) {
		this.duration = DURATION;
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.uuid = uuid;
		this.amount = amount;
	}
	
	public void update() {
		for (AbstractCard c : GetAllInBattleInstances.get(this.uuid)) {
			c.modifyCostForCombat(this.amount);
		}
	    this.isDone = true;
	}
}
