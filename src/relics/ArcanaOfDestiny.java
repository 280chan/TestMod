package relics;

import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import powers.ArcanaOfDestinyPower;

public class ArcanaOfDestiny extends AbstractTestRelic {
	public static final String ID = "ArcanaOfDestiny";
	
	public ArcanaOfDestiny() {
		super(ID, RelicTier.UNCOMMON, LandingSound.SOLID);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	private void tryApplyDebuff() {
		if (hasEnemies())
			AbstractDungeon.getMonsters().monsters.stream().filter(not(ArcanaOfDestinyPower::hasThis))
					.forEach(ArcanaOfDestinyPower::addThis);
	}
	
	public void atPreBattle() {
		tryApplyDebuff();
    }

	public void atTurnStart() {
		tryApplyDebuff();
    }

}