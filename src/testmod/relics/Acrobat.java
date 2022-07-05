package testmod.relics;

import java.util.function.Supplier;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import testmod.relicsup.AcrobatUp;

public class Acrobat extends AbstractTestRelic {
	public static Color color = null;
	public int state = 0;

	public static Color setColorIfNull(Supplier<Color> c) {
		if (color == null)
			color = c.get();
		return color;
	}
	
	public Acrobat() {
		super(RelicTier.COMMON, LandingSound.CLINK, BAD);
	}
	
	public void onPlayCard(final AbstractCard c, final AbstractMonster m) {
		int index = 1 + p().hand.group.indexOf(c);
		int state = 0;
		if (this.counter == -1)
			this.counter = index;
		else
			state = this.counter <= index ? 1 : -1;
		this.counter = index;
		if (this.state * state < 0) {
			p().gainGold(1);
			this.show();
		}
		this.updateHandGlow();
		this.state = state;
    }
	
	public void onRefreshHand() {
		if (color == null)
			color = AcrobatUp.setColorIfNull(this::initGlowColor);
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		if (!this.inCombat())
			return;
		for (AbstractCard c : p().hand.group) {
			int index = 1 + p().hand.group.indexOf(c);
			int state = 0;
			if (this.counter == -1)
				return;
			else if (this.counter <= index)
				state = 1;
			else
				state = -1;
			if (this.state * state < 0 && c.hasEnoughEnergy() && c.cardPlayable(this.randomMonster())) {
				if (this.isActive)
					this.addToGlowChangerList(c, color);
				active = true;
			} else if (this.isActive)
				this.removeFromGlowList(c, color);
		}
		if (active)
			this.beginLongPulse();
		else
			this.stopPulse();
	}

	public void atTurnStart() {
		this.counter = -1;
    }

	public void onVictory() {
		this.stopPulse();
		this.counter = -1;
	}
	
	public boolean canSpawn() {
		return (Settings.isEndless) || (AbstractDungeon.floorNum <= 48);
	}

}