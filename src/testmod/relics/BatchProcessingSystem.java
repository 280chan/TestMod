package testmod.relics;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BatchProcessingSystem extends AbstractTestRelic {
	private static Color color = null;
	
	public BatchProcessingSystem() {
		super(RelicTier.BOSS, LandingSound.SOLID);
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
		if (!this.inCombat())
			return;
		this.stopPulse();
		ColorRegister cr = new ColorRegister(color, this);
		this.streamIfElse(AbstractDungeon.player.hand.group.stream(),
				c -> c.costForTurn == this.counter && c.hasEnoughEnergy()
						&& c.cardPlayable(AbstractDungeon.getRandomMonster()),
				cr::addToGlowChangerList, cr::removeFromGlowList);
	}
	
	public void onEquip() {
		this.reduceEnergy();
		if (color == null)
			color = this.initGlowColor();
    }
	
	public void onUnequip() {
		this.addEnergy();
    }
	
	public void atTurnStart() {
		this.counter = -1;
    }
	
	public void onVictory() {
		this.stopPulse();
	}
	
}