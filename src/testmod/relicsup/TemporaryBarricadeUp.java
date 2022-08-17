package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.powers.BarricadePower;

import testmod.relics.TemporaryBarricade;

public class TemporaryBarricadeUp extends AbstractUpgradedRelic {
	
	public void onEquip() {
		this.addEnergy();
	}
	
	public void onUnequip() {
		this.reduceEnergy();
	}
	
	public void atPreBattle() {
		this.flash();
		this.stopPulse();
		this.atb(apply(p(), new BarricadePower(p())));
    }
	
	public void atTurnStart() {
		this.addTmpActionToBot(() -> {
			int x = p().currentBlock;
			if (x > 0) {
				this.att(new GainEnergyAction(1));
				int newx = TemporaryBarricade.f(x);
				if (newx < x) {
					p().loseBlock(x - newx);
					this.att(new AddTemporaryHPAction(p(), p(), x - newx));
				}
				this.show();
			}
		});
    }

}