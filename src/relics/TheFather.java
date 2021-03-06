package relics;

import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import powers.TheFatherPower;
import utils.MiscMethods;

public class TheFather extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "TheFather";
	
	public TheFather() {
		super(ID, RelicTier.RARE, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
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
	}
	
	private void tryAdd() {
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
			if (!TheFatherPower.hasThis(m))
				m.powers.add(new TheFatherPower(m, this));
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
		this.stopPulse();
	}

}