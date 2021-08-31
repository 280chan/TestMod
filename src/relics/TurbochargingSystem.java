package relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class TurbochargingSystem extends AbstractTestRelic {
	public static final String ID = "TurbochargingSystem";
	
	public TurbochargingSystem() {
		super(ID, RelicTier.COMMON, LandingSound.HEAVY);
		this.setTestTier(BAD);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public float atDamageModify(float damage, AbstractCard c) {
		if (c.freeToPlayOnce) {
			return damage;
		} else if (c.cost == -1) {
			return damage + Math.max(c.energyOnUse, EnergyPanel.totalCount);
		}
		return damage + c.costForTurn;
	}
}