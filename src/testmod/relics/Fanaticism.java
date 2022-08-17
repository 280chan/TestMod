package testmod.relics;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

import testmod.relicsup.FanaticismUp;

public class Fanaticism extends AbstractTestRelic {
	
	public String getUpdatedDescription() {
		return this.counter < 0 ? DESCRIPTIONS[0] : DESCRIPTIONS[0] + DESCRIPTIONS[1] + this.counter + DESCRIPTIONS[2];
	}
	
	public void onEquip() {
		this.addEnergy();
		if (this.inCombat())
			this.atPreBattle();
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
		if ("Wrath".equals(p().stance.ID)) {
			this.atb(new GainEnergyAction(1));
			this.flash();
		}
    }
	
	public void onPlayerEndTurn() {
		if (this.isActive && this.relicStream(FanaticismUp.class).count() == 0) {
			this.atb(new ChangeStanceAction("Wrath".equals(p().stance.ID) ? "Neutral" : "Wrath"));
			this.flash();
		}
    }
	
	public void onLoseHp(int damage) {
		this.modifyCounterAndUpdate(this.counter + ("Wrath".equals(p().stance.ID) ? 2 * damage : damage));
	}
	
	private int sumCounter() {
		return this.relicStream(Fanaticism.class).mapToInt(r -> r.counter).sum();
	}

	public void onAttack(DamageInfo info, int damage, AbstractCreature target) {
		if (this.isActive && this.relicStream(FanaticismUp.class).count() == 0 && sumCounter() > 0 && damage > 0) {
			int tmp = sumCounter() * Math.min(damage, target.currentHealth) / 100;
			if (tmp > 0)
				this.att(new HealAction(p(), p(), tmp));
		}
	}

}