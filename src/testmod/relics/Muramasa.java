package testmod.relics;

import java.util.function.Supplier;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;

import testmod.relicsup.MuramasaUp;

public class Muramasa extends AbstractTestRelic {
	private static Color color = null;
	
	public static Color setColorIfNull(Supplier<Color> c) {
		if (color == null)
			color = c.get();
		return color;
	}
	
	private void initColor() {
		if (color == null)
			color = MuramasaUp.setColorIfNull(this::initGlowColor);
	}
	
	public Muramasa() {
		this.counter = 0;
	}
	
	private void tryDo(AbstractCard c) {
		if (c.type == CardType.ATTACK && (c.costForTurn == 0 || c.freeToPlay())) {
			counter++;
			if (counter == 2) {
				counter = 0;
				this.show();
				this.addToBot(new DrawCardAction(p(), 1));
			}
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
		if (active && this.counter == 1)
			this.beginLongPulse();
		else
			this.stopPulse();
	}
	
	public void onVictory() {
		this.stopPulse();
	}
}