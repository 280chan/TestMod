package testmod.relicsup;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import testmod.mymod.TestMod;
import testmod.powers.TheFatherPower;
import testmod.powers.TheFatherPower.TheFatherCounter;
import testmod.utils.CounterKeeper;

public class TheFatherUp extends AbstractUpgradedRelic implements TheFatherCounter, CounterKeeper {
	private static boolean canUpdate = false;
	private static int numberOfMonsters = 0;
	
	public TheFatherUp() {
		super(RelicTier.RARE, LandingSound.HEAVY);
		this.counter = 0;
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.isActive && this.inCombat()) {
			this.atPreBattle();
			this.atBattleStart();
		}
	}
	
	public void atPreBattle() {
		if (this.hasEnemies()) {
			tryAdd();
		}
    }
	
	public void atBattleStart() {
		if (check(counter + 1)) {
			this.beginLongPulse();
		}
		canUpdate = true;
	}

	public static void tryAdd() {
		AbstractDungeon.getMonsters().monsters.stream().filter(TheFatherPower::needThis)
				.forEach(m -> m.powers.add(new TheFatherPower(m)));
	}
	
	public void update() {
		super.update();
		if (canUpdate && this.inCombat()) {
			if (AbstractDungeon.getMonsters().monsters.size() > numberOfMonsters) {
				tryAdd();
				numberOfMonsters = AbstractDungeon.getMonsters().monsters.size();
			}
		}
	}
	
	public void atTurnStart() {
		tryAdd();
    }

	private boolean check(int n) {
		return n > 1 && (Prime.isPrime(n) || Prime.isPrime(Prime.primeFactorOf(n).size()));
	}
	
	public void count() {
		this.counter++;
		if (Prime.isPrime(this.counter)) {
			p().gainGold(Prime.indexOf(this.counter));
			this.stopPulse();
			this.flash();
		} else if (this.counter > 1 && Prime.isPrime(Prime.primeFactorOf(this.counter).size())) {
			p().gainGold(Prime.primeFactorOf(this.counter).get(0));
			this.stopPulse();
			this.flash();
		}
		if (check(this.counter + 1)) {
			this.beginLongPulse();
		}
	}
	
	public void onVictory() {
		if (this.isActive)
			TheFatherPower.reset();
		this.stopPulse();
		canUpdate = false;
		numberOfMonsters = 0;
	}

}