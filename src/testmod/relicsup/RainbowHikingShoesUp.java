package testmod.relicsup;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import testmod.relics.RainbowHikingShoes;

public class RainbowHikingShoesUp extends AbstractUpgradedRelic {
	private CardRarity lastRarity = null;
	private CardType lastType = null;
	
	public void onUseCard(AbstractCard c, UseCardAction action) {
		if (this.lastType == null || this.lastRarity == null) {
			this.lastType = c.type;
			this.lastRarity = c.rarity;
		} else if (check(c)) {
			this.lastType = c.type;
			this.lastRarity = c.rarity;
			this.atb(new DrawCardAction(1));
			this.show();
		}
	}
	
	private boolean check(AbstractCard c) {
		return c.rarity != this.lastRarity || c.type != this.lastType;
	}
	
	public void onRefreshHand() {
		if (RainbowHikingShoes.color == null)
			RainbowHikingShoes.color = this.initGlowColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		if (!this.inCombat() || this.lastType == null || this.lastRarity == null)
			return;
		for (AbstractCard c : p().hand.group) {
			if (check(c) && c.hasEnoughEnergy() && c.cardPlayable(this.randomMonster())) {
				this.addToGlowChangerList(c, RainbowHikingShoes.color);
				active = true;
			} else
				this.removeFromGlowList(c, RainbowHikingShoes.color);
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