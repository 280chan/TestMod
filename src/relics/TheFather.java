package relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import powers.TheFatherPower;

public class TheFather extends AbstractTestRelic {
	private static boolean canUpdate = false;
	private static int numberOfMonsters = 0;
	
	public TheFather() {
		super(RelicTier.RARE, LandingSound.HEAVY);
	}
	
	public void onEquip() {
		this.counter = 0;
	}
	
	public void atPreBattle() {
		if (this.hasEnemies()) {
			tryAdd();
		}
    }
	
	public void atBattleStart() {
		if (TheFatherPower.isPrime(counter + 1)) {
			this.beginLongPulse();
		}
		canUpdate = true;
	}

	private void tryAdd() {
		AbstractDungeon.getMonsters().monsters.stream().filter(not(TheFatherPower::hasThis))
				.forEach(m -> m.powers.add(new TheFatherPower(m, this)));
	}
	
	public void update() {
		super.update();
		if (canUpdate && this.inCombat()) {
			if (AbstractDungeon.getCurrRoom().monsters.monsters.size() > numberOfMonsters) {
				tryAdd();
				numberOfMonsters = AbstractDungeon.getMonsters().monsters.size();
			}
		}
	}
	
	public void atTurnStart() {
		tryAdd();
    }
	
	public void count() {
		this.counter++;
		if (TheFatherPower.isPrime(counter)) {
			AbstractDungeon.player.gainGold(1);
			this.stopPulse();
			this.flash();
		}
		if (TheFatherPower.isPrime(counter + 1)) {
			this.beginLongPulse();
		}
	}
	
	public void onVictory() {
		TheFatherPower.reset();
		this.stopPulse();
		canUpdate = false;
		numberOfMonsters = 0;
	}

}