package relics;

import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

import mymod.TestMod;
import utils.MiscMethods;

public class CardMagician extends MyRelic implements MiscMethods {
	public static final String ID = "CardMagician";
	
	public CardMagician() {
		super(ID, RelicTier.BOSS, LandingSound.MAGICAL);
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
		TestMod.setActivity(this);
		if (!this.isActive)
			return;
		AbstractDungeon.player.energy.energyMaster++;
    }
	
	public void onUnequip() {
		if (!this.isActive)
			return;
		AbstractDungeon.player.energy.energyMaster--;
    }
	
	public void atTurnStart() {
		if (!this.isActive)
			return;
	    if (!AbstractDungeon.player.discardPile.isEmpty())
	    	this.addToBot(new EmptyDeckShuffleAction());
	    this.addToBot(new ShuffleAction(AbstractDungeon.player.drawPile));
    }
	
}