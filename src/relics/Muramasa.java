package relics;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class Muramasa extends AbstractTestRelic {
	public static final String ID = "Muramasa";
	
	private static Color color = null;
	
	public Muramasa() {
		super(ID, RelicTier.RARE, LandingSound.CLINK);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	private void tryDo(AbstractCard c) {
		if (c.type == CardType.ATTACK && (c.costForTurn == 0 || c.freeToPlayOnce)) {
			counter++;
			if (counter == 2) {
				counter = 0;
				this.show();
				this.addToBot(new DrawCardAction(AbstractDungeon.player, 1));
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
		if (color == null)
			color = this.initGlowColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		if (!this.inCombat())
			return;
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			if (c.type == CardType.ATTACK && (c.costForTurn == 0 || c.freeToPlayOnce) && c.hasEnoughEnergy() && c.cardPlayable(AbstractDungeon.getRandomMonster())) {
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