package testmod.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class TurbochargingSystem extends AbstractTestRelic {
	
	public float atDamageModify(float damage, AbstractCard c) {
		return c.freeToPlay() ? damage
				: (c.cost == -1 ? damage + Math.max(c.energyOnUse, EnergyPanel.totalCount) : damage + c.costForTurn);
	}
}