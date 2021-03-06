package relics;

import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import powers.ArcanaOfDestinyPower;
import utils.MiscMethods;

public class ArcanaOfDestiny extends AbstractTestRelic implements MiscMethods {
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
		if (!hasEnemies()) {
			return;
		}
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
			if (!ArcanaOfDestinyPower.hasThis(m))
				m.powers.add(new ArcanaOfDestinyPower(m));
	}
	
	public void atPreBattle() {
		if (AbstractDungeon.currMapNode == null)
			return;
		tryApplyDebuff();
    }

	public void atTurnStart() {
		tryApplyDebuff();
    }

}