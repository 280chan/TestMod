package relics;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class RainbowHikingShoes extends AbstractTestRelic {
	public static final String ID = "RainbowHikingShoes";
	private CardRarity lastRarity = null;
	private static Color color = null;
	
	public RainbowHikingShoes() {
		super(RelicTier.RARE, LandingSound.CLINK);
	}
	
	public void onUseCard(AbstractCard c, UseCardAction action) {
		if (this.lastRarity == null) {
			this.lastRarity = c.rarity;
		} else if (this.lastRarity != c.rarity) {
			this.lastRarity = c.rarity;
			this.addToBot(new DrawCardAction(1));
		}
	}
	
	public void onRefreshHand() {
		if (color == null)
			color = this.initGlowColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		if (!this.inCombat() || this.lastRarity == null)
			return;
		for (AbstractCard c : p().hand.group) {
			if (c.rarity != this.lastRarity && c.hasEnoughEnergy()
					&& c.cardPlayable(AbstractDungeon.getRandomMonster())) {
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
	
	public void atPreBattle() {
		this.lastRarity = null;
    }
}