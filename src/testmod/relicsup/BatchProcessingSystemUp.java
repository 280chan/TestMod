package testmod.relicsup;

import java.util.function.Supplier;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import testmod.relics.BatchProcessingSystem;

public class BatchProcessingSystemUp extends AbstractUpgradedRelic {
	private static Color color = null;
	
	public static Color setColorIfNull(Supplier<Color> c) {
		if (color == null)
			color = c.get();
		return color;
	}
	
	private void initColor() {
		if (color == null)
			color = BatchProcessingSystem.setColorIfNull(this::initGlowColor);
	}
	
	public BatchProcessingSystemUp() {
		super(RelicTier.BOSS, LandingSound.SOLID);
	}
	
	private boolean check(AbstractCard c) {
		return this.counter == c.costForTurn || (c.freeToPlayOnce && this.counter == 0);
	}
	
	public void onPlayCard(final AbstractCard c, final AbstractMonster m) {
		if (c.isInAutoplay)
			return;
		if (this.check(c)) {
			this.show();
			this.addToBot(new GainEnergyAction(Math.max(1, c.freeToPlayOnce ? 0 : c.costForTurn)));
		}
		this.counter = c.freeToPlayOnce ? 0 : Math.max(c.costForTurn, 0);
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
		this.initColor();
    }
	
	public void onVictory() {
		this.stopPulse();
		this.counter = -1;
	}
	
	public void atPreBattle() {
		this.counter = -1;
	}
	
}