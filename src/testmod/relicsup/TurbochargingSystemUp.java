package testmod.relicsup;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class TurbochargingSystemUp extends AbstractUpgradedRelic {
	
	private int cost(AbstractCard c) {
		return c.freeToPlay() ? 0 : c.cost == -1 ? Math.max(c.energyOnUse, EnergyPanel.totalCount) : c.costForTurn;
	}
	
	public float atDamageModify(float damage, AbstractCard c) {
		return damage + Math.max(cost(c) * 1f * cost(c) * cost(c), 1);
	}
}