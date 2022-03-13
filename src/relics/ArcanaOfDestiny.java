package relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import powers.ArcanaOfDestinyPower;

public class ArcanaOfDestiny extends AbstractTestRelic {
	
	public ArcanaOfDestiny() {
		super(RelicTier.UNCOMMON, LandingSound.SOLID);
	}
	
	private void tryApplyDebuff() {
		if (hasEnemies())
			AbstractDungeon.getMonsters().monsters.stream().filter(not(ArcanaOfDestinyPower::hasThis))
					.forEach(ArcanaOfDestinyPower::addThis);
	}
	
	public void atPreBattle() {
		tryApplyDebuff();
    }

	public void update() {
		super.update();
		if (this.isActive && this.inCombat())
			tryApplyDebuff();
	}

}