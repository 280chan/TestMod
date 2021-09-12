package relics;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import utils.MiscMethods;

public class BatchProcessingSystem extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "BatchProcessingSystem";
	private static Color color = null;
	
	public BatchProcessingSystem() {
		super(ID, RelicTier.BOSS, LandingSound.SOLID);
	}
	
	public String getUpdatedDescription() {
		return this.DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void onPlayCard(final AbstractCard c, final AbstractMonster m) {
		if (c.isInAutoplay)
			return;
		if (this.counter == c.costForTurn) {
			this.show();
			this.addToBot(new GainEnergyAction(1));
		}
		this.counter = c.costForTurn;
		this.updateHandGlow();
	}
	
	public void onRefreshHand() {
		if (color == null)
			color = this.initGlowColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		if (!this.canUpdateHandGlow())
			return;
		this.stopPulse();
		ColorRegister cr = new ColorRegister(color, this);
		this.streamIfElse(AbstractDungeon.player.hand.group.stream(),
				c -> c.costForTurn == this.counter && c.hasEnoughEnergy()
						&& c.cardPlayable(AbstractDungeon.getRandomMonster()),
				cr::addToGlowChangerList, cr::removeFromGlowList);
	}
	
	public void onEquip() {
		AbstractDungeon.player.energy.energyMaster--;
		if (color == null)
			color = this.initGlowColor();
    }
	
	public void onUnequip() {
		AbstractDungeon.player.energy.energyMaster++;
    }
	
	public void atTurnStart() {
		this.counter = -1;
    }
	
	public void onVictory() {
		this.stopPulse();
	}
	
}