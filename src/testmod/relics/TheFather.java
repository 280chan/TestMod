package testmod.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import testmod.mymod.TestMod;
import testmod.powers.TheFatherPower;
import testmod.powers.TheFatherPower.TheFatherCounter;
import testmod.relicsup.TheFatherUp;

public class TheFather extends AbstractTestRelic implements TheFatherCounter {
	private static boolean canUpdate = false;
	private static int numberOfMonsters = 0;
	
	public void onEquip() {
		TestMod.setActivity(this);
		this.counter = 0;
		if (this.isActive && this.inCombat() && this.relicStream(TheFatherUp.class).count() == 0) {
			this.atPreBattle();
			this.atBattleStart();
		}
	}
	
	public void atPreBattle() {
		if (this.hasEnemies() && this.relicStream(TheFatherUp.class).count() == 0) {
			TheFatherUp.tryAdd();
		}
    }
	
	public void atBattleStart() {
		if (Prime.isPrime(counter + 1)) {
			this.beginLongPulse();
		}
		canUpdate = true;
	}
	
	public void update() {
		super.update();
		if (canUpdate && this.inCombat() && this.relicStream(TheFatherUp.class).count() == 0) {
			if (AbstractDungeon.getMonsters().monsters.size() > numberOfMonsters) {
				TheFatherUp.tryAdd();
				numberOfMonsters = AbstractDungeon.getMonsters().monsters.size();
			}
		}
	}
	
	public void atTurnStart() {
		TheFatherUp.tryAdd();
    }
	
	public void count() {
		this.counter++;
		if (Prime.isPrime(counter)) {
			p().gainGold(1);
			this.stopPulse();
			this.flash();
		}
		if (Prime.isPrime(counter + 1)) {
			this.beginLongPulse();
		}
	}
	
	public void onVictory() {
		if (this.isActive && this.relicStream(TheFatherUp.class).count() == 0)
			TheFatherPower.reset();
		this.stopPulse();
		canUpdate = false;
		numberOfMonsters = 0;
	}

}