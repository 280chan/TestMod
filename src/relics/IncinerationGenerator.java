package relics;

import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

import utils.MiscMethods;

public class IncinerationGenerator extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "IncinerationGenerator";
	
	public IncinerationGenerator() {
		super(ID, RelicTier.BOSS, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		if (AbstractDungeon.player != null) {
			return setDescription(AbstractDungeon.player.chosenClass);
		}
		return setDescription(null);
	}

	private String setDescription(PlayerClass c) {
		return this.setDescription(c, this.DESCRIPTIONS[0], this.DESCRIPTIONS[1]);
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, setDescription(c)));
	    initializeTips();
	}
	
	public void onEquip() {
		AbstractDungeon.player.energy.energyMaster += 1;
    }
	
	public void onUnequip() {
		AbstractDungeon.player.energy.energyMaster -= 1;
    }
	
	public void atTurnStartPostDraw() {
		AbstractPlayer p = AbstractDungeon.player;
		this.addToBot(new ExhaustAction(p, p, 1, false));
	}
	
}