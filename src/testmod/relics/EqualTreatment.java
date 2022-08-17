package testmod.relics;

import java.util.function.Supplier;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import testmod.relicsup.EqualTreatmentUp;

public class EqualTreatment extends AbstractTestRelic {
	private static Color color = null;

	public static Color setColorIfNull(Supplier<Color> c) {
		if (color == null)
			color = c.get();
		return color;
	}
	
	private void initColor() {
		if (color == null)
			color = EqualTreatmentUp.setColorIfNull(this::initGlowColor);
	}
	
	public void atTurnStart() {
		this.changeState(true);
	}
	
	private void changeState(boolean state) {
		if (state) {
			this.beginLongPulse();
			this.counter = -2;
		} else {
			this.stopPulse();
			this.counter = -1;
		}
	}
	
	private boolean alive(AbstractMonster m) {
		return !(m.isDead || m.isDying || m.halfDead || m.isEscaping);
	}
	
	private boolean checkTarget(AbstractCard c) {
		return (c.target == CardTarget.ENEMY || c.target == CardTarget.SELF_AND_ENEMY) && this.counter == -2;
	}
	
	@SuppressWarnings("unchecked")
	public void onUseCard(final AbstractCard c, final UseCardAction action) {
		if (checkTarget(c)) {
			AbstractDungeon.getMonsters().monsters.stream().filter(this::alive).filter(not(action.target::equals))
					.forEach(combine(c::calculateCardDamage, m -> c.use(p(), m)));
			this.changeState(false);
			this.show();
		}
	}
	
	public void onRefreshHand() {
		this.initColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		ColorRegister cr = new ColorRegister(color);
		this.streamIfElse(p().hand.group.stream(), this::checkTarget, cr::addToGlowChangerList, cr::removeFromGlowList);
	}
	
	public void onVictory() {
		this.changeState(false);
	}

}