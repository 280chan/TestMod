package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import powers.PlaguePower;

public class PlagueAction extends AbstractGameAction {
	private boolean freeToPlayOnce = false;
	private int energyOnUse = -1;
	private AbstractPlayer p;

	public PlagueAction(AbstractPlayer p, boolean freeToPlayOnce, int energyOnUse) {
		this.freeToPlayOnce = freeToPlayOnce;
		this.duration = Settings.ACTION_DUR_XFAST;
		this.actionType = ActionType.SPECIAL;
		this.energyOnUse = energyOnUse;
		this.p = p;
	}
  
	public void update() {
		int effect = EnergyPanel.totalCount;
		if (this.energyOnUse != -1) {
			effect = this.energyOnUse;
		}
		if (this.p.hasRelic("Chemical X")) {
			effect += 2;
			this.p.getRelic("Chemical X").flash();
		}
		if (effect > 0) {
			this.addToBot(new ApplyPowerAction(p, p, new PlaguePower(p, effect), effect));
			if (!this.freeToPlayOnce)
				this.p.energy.use(EnergyPanel.totalCount);
		}
		this.isDone = true;
	}
}
