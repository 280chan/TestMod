package relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

public class Restrained extends AbstractTestRelic {
	public static final String ID = "Restrained";
	
	public Restrained() {
		super(ID, RelicTier.SHOP, LandingSound.MAGICAL);
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
		AbstractDungeon.player.energy.energyMaster++;
    }
	
	public void onUnequip() {
		AbstractDungeon.player.energy.energyMaster--;
    }

	public void atTurnStart() {
		this.counter++;
	}
	
	public void atPreBattle() {
		this.counter = 0;
    }

	public void onVictory() {
		this.counter = -1;
	}
	
	public boolean canPlay(AbstractCard c) {
		return this.hasPrudence() || this.counter > 1 || c.type != CardType.POWER;
	}
}