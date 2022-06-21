package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class IncinerationGeneratorUp extends AbstractUpgradedRelic implements ClickableRelic {
	
	public IncinerationGeneratorUp() {
		super(RelicTier.BOSS, LandingSound.HEAVY);
	}
	
	public void onEquip() {
		this.addEnergy();
    }
	
	public void onUnequip() {
		this.reduceEnergy();
    }
	
	public void atTurnStartPostDraw() {
		this.counter = -2;
		this.beginLongPulse();
	}
	
	public void onPlayerEndTurn() {
		this.counter = -1;
		this.stopPulse();
	}
	
	public void onVictory() {
		this.onPlayerEndTurn();
	}
	
	public void onExhaust(AbstractCard c) {
		this.atb(new GainEnergyAction(1));
	}

	@Override
	public void onRightClick() {
		if (this.counter == -2 && !p().hand.isEmpty()) {
			this.onPlayerEndTurn();
			this.atb(new ExhaustAction(p(), p(), 1, false));
		}
	}
	
}