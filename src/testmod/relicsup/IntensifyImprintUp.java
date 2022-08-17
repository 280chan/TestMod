package testmod.relicsup;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import testmod.powers.IntensifyImprintPower;

public class IntensifyImprintUp extends AbstractUpgradedRelic {
	
	public String getUpdatedDescription() {
		return this.counter < 0 ? DESCRIPTIONS[0]
				: DESCRIPTIONS[0] + " NL " + DESCRIPTIONS[1] + this.counter + DESCRIPTIONS[2];
	}
	
	public void onEquip() {
		this.counter = 0;
	}
	
	private void modifyCounter(int newValue) {
		this.counter = newValue;
		this.updateDescription(null);
	}
	
	public void incrementCounter() {
		this.counter++;
		this.updateDescription();
		this.show();
	}
	
	public void atTurnStart() {
		this.modifyCounter(this.counter / 2);
    }
	
	public void update() {
		super.update();
		if (this.counter >= 0 && this.inCombat() && this.hasEnemies())
			AbstractDungeon.getMonsters().monsters.stream().filter(not(IntensifyImprintPower::hasThis))
					.forEach(m -> m.powers.add(new IntensifyImprintPower(m)));
	}
	
}