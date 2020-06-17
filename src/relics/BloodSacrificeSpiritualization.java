package relics;

import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

import actions.BloodSacrificeSpiritualizationSelectAction;

public class BloodSacrificeSpiritualization extends AbstractTestRelic {
	public static final String ID = "BloodSacrificeSpiritualization";
	
	public BloodSacrificeSpiritualization() {
		super(ID, RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void atBattleStart() {
		this.addToBot(new BloodSacrificeSpiritualizationSelectAction(AbstractDungeon.player));
    }

	public boolean canSpawn() {
		if (!Settings.isEndless && AbstractDungeon.actNum > 1) {
			return false;
		}
		return true;
	}
	
}