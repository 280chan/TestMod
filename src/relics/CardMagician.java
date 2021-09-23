package relics;

import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

public class CardMagician extends AbstractTestRelic {
	public static final String ID = "CardMagician";
	
	public CardMagician() {
		super(ID, RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return this.DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}

	public void onEquip() {
		AbstractDungeon.player.energy.energyMaster++;
    }
	
	public void onUnequip() {
		AbstractDungeon.player.energy.energyMaster--;
    }
	
	public void atTurnStart() {
	    if (!AbstractDungeon.player.discardPile.isEmpty())
	    	this.addToBot(new EmptyDeckShuffleAction());
	    this.addToBot(new ShuffleAction(AbstractDungeon.player.drawPile));
    }
	
}