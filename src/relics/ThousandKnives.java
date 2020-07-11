package relics;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import utils.MiscMethods;

public class ThousandKnives extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "ThousandKnives";
	
	private static Color color = null;
	
	public ThousandKnives() {
		super(ID, RelicTier.RARE, LandingSound.SOLID);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void onRefreshHand() {
		if (color == null)
			color = this.initGlowColor();
		if (this.canUpdateHandGlow())
			this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			if (checkCard(c) && c.hasEnoughEnergy() && c.cardPlayable(AbstractDungeon.getRandomMonster())) {
				this.addToGlowChangerList(c, color);
				active = true;
			} else
				this.removeFromGlowList(c, color);
		}
		if (active)
			this.beginLongPulse();
		else
			this.stopPulse();
	}
	
	public void onVictory() {
		this.stopPulse();
	}
	
	private static boolean checkCard(AbstractCard c) {
		return (c.costForTurn == 0 || c.freeToPlayOnce) && !c.isInAutoplay && c.type == CardType.ATTACK;
	}
	
	private int countCards() {
		int count = 0;
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			if (checkCard(c)) {
				count++;
			}
		}
		return count;
	}
	
	public void onPlayCard(final AbstractCard c, final AbstractMonster m) {
		if (this.isActive && checkCard(c)) {
			this.addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, countCards(), true));
			this.addToBot(new MakeTempCardInDiscardAction(c.makeStatEquivalentCopy(), 1));
			this.show();
		}
	}
	
	public void onCardDraw(final AbstractCard c) {
		if (this.isActive && checkCard(c)) {
			this.addToTop(new MakeTempCardInHandAction(c.makeStatEquivalentCopy()));
			this.show();
		}
    }

}