package testmod.relicsup;

import java.util.function.Supplier;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import testmod.relics.Muramasa;

public class MuramasaUp extends AbstractUpgradedRelic {
	private static Color color = null;
	
	public static Color setColorIfNull(Supplier<Color> c) {
		if (color == null)
			color = c.get();
		return color;
	}
	
	private void initColor() {
		if (color == null)
			color = Muramasa.setColorIfNull(this::initGlowColor);
	}
	
	public void atPreBattle() {
		this.counter = 0;
	}
	
	private void tryDo(AbstractCard c) {
		if ((c.type == CardType.ATTACK || c.type == CardType.POWER) && (c.costForTurn == 0 || c.freeToPlay())) {
			counter++;
			boolean tmp = false;
			if (counter % 2 == 0) {
				tmp = true;
				this.atb(new DrawCardAction(p(), 1));
			}
			if (Prime.isPrime(counter)) {
				tmp = true;
				this.atb(new GainEnergyAction(1));
			}
			if (tmp)
				this.show();
		}
	}
	
	public void onUseCard(final AbstractCard c, final UseCardAction useCardAction) {
		tryDo(c);
	}
	
	public void onCardDraw(final AbstractCard c) {
		tryDo(c);
	}
	
	public void onRefreshHand() {
		this.initColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		if (!this.inCombat())
			return;
		for (AbstractCard c : p().hand.group) {
			if (c.type == CardType.ATTACK && (c.costForTurn == 0 || c.freeToPlay()) && c.hasEnoughEnergy()
					&& c.cardPlayable(this.randomMonster())) {
				this.addToGlowChangerList(c, color);
				active = true;
			} else
				this.removeFromGlowList(c, color);
		}
		if (active && (this.counter % 2 == 1 || Prime.isPrime(this.counter + 1)))
			this.beginLongPulse();
		else
			this.stopPulse();
	}
	
	public void onVictory() {
		this.counter = -1;
		this.stopPulse();
	}
}