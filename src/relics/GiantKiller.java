package relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import powers.GiantKillerPower;

public class GiantKiller extends AbstractTestRelic {
	
	public GiantKiller() {
		super(RelicTier.UNCOMMON, LandingSound.HEAVY);
	}

	private void tryApplyDebuff() {
		if (hasEnemies())
			AbstractDungeon.getMonsters().monsters.stream().filter(not(GiantKillerPower::hasThis))
					.forEach(GiantKillerPower::addThis);
	}

	public void atPreBattle() {
		tryApplyDebuff();
    }

	public void atTurnStart() {
		tryApplyDebuff();
    }

}