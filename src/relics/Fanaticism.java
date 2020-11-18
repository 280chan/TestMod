package relics;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

public class Fanaticism extends AbstractTestRelic {
	public static final String ID = "Fanaticism";
	
	public Fanaticism() {
		super(ID, RelicTier.BOSS, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		if (this.counter < 0)
			return DESCRIPTIONS[0];
		else
			return DESCRIPTIONS[0] + DESCRIPTIONS[1] + this.counter + DESCRIPTIONS[2];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void onEquip() {
		AbstractDungeon.player.energy.energyMaster++;
    }
	
	public void onUnequip() {
		AbstractDungeon.player.energy.energyMaster--;
    }
	
	private void modifyCounterAndUpdate(int newValue) {
		this.counter = newValue;
		this.updateDescription(AbstractDungeon.player.chosenClass);
	}
	
	public void atPreBattle() {
		this.modifyCounterAndUpdate(0);
	}
	
	public void onVictory() {
		this.modifyCounterAndUpdate(-1);
	}
	
	public void atTurnStart() {
		if (AbstractDungeon.player.stance.ID.equals("Wrath")) {
			this.addToBot(new GainEnergyAction(1));
			this.flash();
		}
    }
	
	public void onPlayerEndTurn() {
		if (AbstractDungeon.player.stance.ID.equals("Wrath")) {
			this.addToBot(new ChangeStanceAction("Neutral"));
		} else {
			this.addToBot(new ChangeStanceAction("Wrath"));
		}
		this.flash();
    }
	
	public void onLoseHp(int damage) {
		if (AbstractDungeon.player.stance.ID.equals("Wrath")) {
			this.modifyCounterAndUpdate(this.counter + 2 * damage);
		} else {
			this.modifyCounterAndUpdate(this.counter + damage);
		}
	}
	
	public void onAttack(DamageInfo info, int damage, AbstractCreature target) {
		if (this.counter > 0 && damage > 0) {
			int tmp = this.counter * Math.min(damage, target.currentHealth) / 100;
			if (tmp > 0)
				this.addToBot(new HealAction(AbstractDungeon.player, AbstractDungeon.player, tmp));
		}
	}

}