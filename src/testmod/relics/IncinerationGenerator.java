package testmod.relics;

import com.megacrit.cardcrawl.actions.common.ExhaustAction;

public class IncinerationGenerator extends AbstractTestRelic {
	
	public void onEquip() {
		this.addEnergy();
	}
	
	public void onUnequip() {
		this.reduceEnergy();
	}
	
	public void atTurnStartPostDraw() {
		this.atb(new ExhaustAction(p(), p(), 1, false));
	}
	
}