package relics;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class EqualTreatment extends AbstractTestRelic {
	private static Color color = null;
	
	public EqualTreatment() {
		super(RelicTier.RARE, LandingSound.MAGICAL);
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
	
	@SuppressWarnings("unchecked")
	public void onUseCard(final AbstractCard c, final UseCardAction action) {
		if (c.target == CardTarget.ENEMY && this.counter == -2) {
			AbstractDungeon.getMonsters().monsters.stream().filter(this::alive).filter(not(action.target::equals))
					.forEach(combine(c::calculateCardDamage, m -> c.use(p(), m)));
			this.changeState(false);
			this.show();
		}
	}
	
	public void onRefreshHand() {
		if (color == null)
			color = this.initGlowColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		ColorRegister cr = new ColorRegister(color);
		this.streamIfElse(p().hand.group.stream(), c -> c.target == CardTarget.ENEMY && this.counter == -2,
				cr::addToGlowChangerList, cr::removeFromGlowList);
	}
	
	public void onVictory() {
		this.changeState(false);
	}

}