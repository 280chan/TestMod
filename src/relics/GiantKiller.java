package relics;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import powers.GiantKillerPower;

public class GiantKiller extends AbstractTestRelic {
	
	public GiantKiller() {
		super(RelicTier.RARE, LandingSound.HEAVY);
	}

	public static int countGiantKiller() {
		return (int) INSTANCE.relicStream(GiantKiller.class).count();
	}
	
	private void tryApplyDebuff() {
		if (this.isActive && hasEnemies())
			AbstractDungeon.getMonsters().monsters.stream().forEach(GiantKiller::addIfNotHave);
	}
	
	public static boolean notHave(AbstractCreature m) {
		return m.powers.stream().noneMatch(p -> p instanceof GiantKillerPower);
	}
	
	public static void addThis(AbstractCreature m) {
		m.powers.add(new GiantKillerPower(m));
	}
	
	public static void addIfNotHave(AbstractCreature m) {
		if (notHave(m) && countGiantKiller() > 0)
			addThis(m);
	}

	public void atPreBattle() {
		tryApplyDebuff();
    }

	public void atTurnStart() {
		tryApplyDebuff();
    }

}