package relics;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ThousandKnives extends AbstractTestRelic {
	
	private static Color color = null;
	
	public ThousandKnives() {
		super(RelicTier.RARE, LandingSound.SOLID);
	}
	
	public void onRefreshHand() {
		if (color == null)
			color = this.initGlowColor();
		if (this.inCombat())
			this.updateHandGlow();
	}
	
	private boolean checkGlow(AbstractCard c) {
		return checkCard(c) && c.hasEnoughEnergy() && c.cardPlayable(AbstractDungeon.getRandomMonster());
	}
	
	private void updateHandGlow() {
		ColorRegister cr = new ColorRegister(color);
		if (AbstractDungeon.player.hand.group.stream().anyMatch(this::checkGlow))
			this.beginLongPulse();
		else
			this.stopPulse();
		this.streamIfElse(AbstractDungeon.player.hand.group.stream(), this::checkGlow, cr::addToGlowChangerList,
				cr::removeFromGlowList);
	}
	
	public void onVictory() {
		this.stopPulse();
	}
	
	private boolean checkCard(AbstractCard c) {
		return (c.costForTurn == 0 || c.freeToPlayOnce) && !c.isInAutoplay && c.type == CardType.ATTACK;
	}
	
	private int countCards() {
		return (int) AbstractDungeon.player.hand.group.stream().filter(this::checkCard).count();
	}
	
	public void onPlayCard(final AbstractCard c, final AbstractMonster m) {
		if (checkCard(c)) {
			this.addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, countCards(), true));
			this.addToBot(new MakeTempCardInDiscardAction(c.makeStatEquivalentCopy(), 1));
			this.show();
		}
	}
	
	public void onCardDraw(final AbstractCard c) {
		if (checkCard(c)) {
			this.addToTop(new MakeTempCardInHandAction(c.makeStatEquivalentCopy()));
			this.show();
		}
    }

}