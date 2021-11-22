package relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import powers.ArcanaOfDestinyPower;

public class ArcanaOfDestiny extends AbstractTestRelic {
	
	public ArcanaOfDestiny() {
		super(RelicTier.UNCOMMON, LandingSound.SOLID);
		this.counter = -1;
	}
	
	private void tryApplyDebuff() {
		if (hasEnemies())
			AbstractDungeon.getMonsters().monsters.stream().filter(not(ArcanaOfDestinyPower::hasThis))
					.forEach(ArcanaOfDestinyPower::addThis);
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