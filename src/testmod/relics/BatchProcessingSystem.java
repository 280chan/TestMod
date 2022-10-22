package testmod.relics;

import java.util.function.Supplier;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import testmod.relicsup.BatchProcessingSystemUp;

public class BatchProcessingSystem extends AbstractTestRelic {
	private static Color color = null;

	public static Color setColorIfNull(Supplier<Color> c) {
		if (color == null)
			color = c.get();
		return color;
	}
	
	private void initColor() {
		if (color == null)
			color = BatchProcessingSystemUp.setColorIfNull(this::initGlowColor);
	}
	
	private boolean check(AbstractCard c) {
		return this.counter == c.costForTurn || (c.freeToPlay() && this.counter == 0);
	}
	
	public void onPlayCard(final AbstractCard c, final AbstractMonster m) {
		if (c.isInAutoplay)
			return;
		if (this.check(c)) {
			this.show();
			this.addToBot(new GainEnergyAction(1));
		}
		this.counter = c.freeToPlay() ? 0 : Math.max(c.costForTurn, 0);
		this.updateHandGlow();
	}
	
	public void onRefreshHand() {
		this.initColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		if (!this.inCombat())
			return;
		this.stopPulse();
		colorRegister(color).addRelic(this).addPredicate(c -> this.check(c) && c.hasEnoughEnergy()
						&& c.cardPlayable(this.randomMonster())).updateHand();
	}
	
	public void onEquip() {
		this.reduceEnergy();
		this.initColor();
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