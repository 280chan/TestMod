package testmod.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class EnergyCheck extends AbstractTestRelic implements ClickableRelic {
	private boolean newTurn = false;
	
	private int max() {
		return p().energy.energyMaster;
	}
	
	public EnergyCheck() {
		super(RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public void atPreBattle() {
		this.counter = 0;
    }
	
	public void onEnergyRecharge() {
		if (this.newTurn) {
			this.newTurn = false;
			if (this.counter > 0)
				this.repayEnergy(Math.min(Math.min(this.counter, this.max()), EnergyPanel.totalCount));
		}
	}
	
	private void repayEnergy(int amount) {
		this.counter -= amount;
		EnergyPanel.useEnergy(amount);
	}
	
	public void onPlayerEndTurn() {
		newTurn = true;
    }

	@Override
	public void onRightClick() {
		if (newTurn)
			return;
		this.addToBot(new GainEnergyAction(1));
		this.show();
		this.counter++;
		if (this.counter > this.max()) {
			this.addToBot(new LoseHPAction(p(), p(), this.counter - p().energy.energyMaster));
		}
	}
	
}