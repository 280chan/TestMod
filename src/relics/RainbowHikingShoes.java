package relics;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

import utils.MiscMethods;

public class RainbowHikingShoes extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "RainbowHikingShoes";
	private CardRarity lastRarity = null;
	private static Color color = null;
	
	public RainbowHikingShoes() {
		super(ID, RelicTier.RARE, LandingSound.CLINK);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
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
		if (!this.canUpdateHandGlow() || this.lastRarity == null)
			return;
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			if (c.rarity != this.lastRarity && c.hasEnoughEnergy() && c.cardPlayable(AbstractDungeon.getRandomMonster())) {
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