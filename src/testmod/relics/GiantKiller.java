package testmod.relics;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import testmod.mymod.TestMod;
import testmod.powers.GiantKillerPower;

public class GiantKiller extends AbstractTestRelic {

	public static int count() {
		return (int) MISC.relicStream(GiantKiller.class).count();
	}
	
	private void tryApplyDebuff() {
		if (this.isActive && hasEnemies() && count() > 0)
			AbstractDungeon.getMonsters().monsters.stream().filter(this::notHave).forEach(this::addThis);
	}
	
	private boolean notHave(AbstractCreature m) {
		return m.powers.stream().noneMatch(p -> p instanceof GiantKillerPower);
	}
	
	private void addThis(AbstractCreature m) {
		m.powers.add(new GiantKillerPower(m));
	}

	public void onEquip() {
		TestMod.setActivity(this);
		if (this.isActive && this.inCombat())
			this.atPreBattle();
	}
	
	public void atPreBattle() {
		tryApplyDebuff();
		this.counter = -2;
    }
	
	public void onVictory() {
		this.counter = -1;
	}

	public void update() {
		super.update();
		if (this.counter == -2)
			tryApplyDebuff();
	}

}