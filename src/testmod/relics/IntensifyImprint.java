package testmod.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import testmod.powers.IntensifyImprintPower;

public class IntensifyImprint extends AbstractTestRelic {
	
	public String getUpdatedDescription() {
		return this.counter < 0 ? DESCRIPTIONS[0]
				: DESCRIPTIONS[0] + " NL " + DESCRIPTIONS[1] + this.counter + DESCRIPTIONS[2];
	}
	
	private void modifyCounter(int newValue) {
		this.counter = newValue;
		this.updateDescription(null);
	}
	
	private void resetCounter() {
		this.modifyCounter(0);
	}
	
	private void stopCounter() {
		this.modifyCounter(-1);
	}
	
	public void incrementCounter() {
		this.counter++;
		this.updateDescription();
		this.show();
	}
	
	public void atPreBattle() {
		this.resetCounter();
	}
	
	public void atTurnStart() {
		this.resetCounter();
	}
	
	public void onVictory() {
		this.stopCounter();
	}
	
	public void update() {
		super.update();
		if (this.counter < 0 || !this.hasEnemies())
			return;
		if (this.inCombat())
			AbstractDungeon.getMonsters().monsters.stream().filter(not(IntensifyImprintPower::hasThis))
					.forEach(m -> m.powers.add(new IntensifyImprintPower(m)));
	}
	
}