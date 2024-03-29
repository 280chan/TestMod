package testmod.relics;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ThousandKnives extends AbstractTestRelic {
	private static Color color = null;
	
	public void onRefreshHand() {
		if (color == null)
			color = this.initGlowColor();
		if (this.inCombat())
			this.updateHandGlow();
	}
	
	private boolean checkGlow(AbstractCard c) {
		return checkCard(c) && c.hasEnoughEnergy() && c.cardPlayable(this.randomMonster());
	}
	
	private void updateHandGlow() {
		ColorRegister cr = new ColorRegister(color);
		if (p().hand.group.stream().anyMatch(this::checkGlow))
			this.beginLongPulse();
		else
			this.stopPulse();
		this.streamIfElse(p().hand.group.stream(), this::checkGlow, cr::addToGlowChangerList, cr::removeFromGlowList);
	}
	
	public void onVictory() {
		this.stopPulse();
	}
	
	private boolean checkCard(AbstractCard c) {
		return (c.costForTurn == 0 || c.freeToPlay()) && !c.isInAutoplay && c.type == CardType.ATTACK;
	}
	
	private int countCards() {
		return (int) p().hand.group.stream().filter(this::checkCard).count();
	}
	
	public void onPlayCard(final AbstractCard c, final AbstractMonster m) {
		if (checkCard(c)) {
			this.atb(new GainBlockAction(p(), p(), countCards(), true));
			this.atb(new MakeTempCardInDiscardAction(c.makeStatEquivalentCopy(), 1));
			this.show();
		}
	}
	
	public void onCardDraw(final AbstractCard c) {
		if (checkCard(c)) {
			this.att(new MakeTempCardInHandAction(c.makeStatEquivalentCopy()));
			this.show();
		}
	}

}