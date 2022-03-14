package testmod.relics;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class Fanaticism extends AbstractTestRelic {
	
	public Fanaticism() {
		super(RelicTier.BOSS, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		return this.counter < 0 ? DESCRIPTIONS[0] : DESCRIPTIONS[0] + DESCRIPTIONS[1] + this.counter + DESCRIPTIONS[2];
	}
	
	public void onEquip() {
		this.addEnergy();
    }
	
	public void onUnequip() {
		this.reduceEnergy();
    }
	
	private void modifyCounterAndUpdate(int newValue) {
		this.counter = newValue;
		this.updateDescription(p().chosenClass);
	}
	
	public void atPreBattle() {
		this.modifyCounterAndUpdate(0);
	}
	
	public void onVictory() {
		this.modifyCounterAndUpdate(-1);
	}
	
	public void atTurnStart() {
		if (p().stance.ID.equals("Wrath")) {
			this.addToBot(new GainEnergyAction(1));
			this.flash();
		}
    }
	
	public void onPlayerEndTurn() {
		this.addToBot(new ChangeStanceAction(p().stance.ID.equals("Wrath") ? "Neutral" : "Wrath"));
		this.flash();
    }
	
	public void onLoseHp(int damage) {
		this.modifyCounterAndUpdate(this.counter + (p().stance.ID.equals("Wrath") ? 2 * damage : damage));
	}

	public void onAttack(DamageInfo info, int damage, AbstractCreature target) {
		if (this.counter > 0 && damage > 0) {
			int tmp = this.counter * Math.min(damage, target.currentHealth) / 100;
			if (tmp > 0)
				this.addToBot(new HealAction(p(), p(), tmp));
		}
	}

}