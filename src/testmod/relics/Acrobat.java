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
	private boolean increasing = false, inited = false;

	public static Color setColorIfNull(Supplier<Color> c) {
		if (color == null)
			color = c.get();
		return color;
	}
	
	public void onPlayCard(final AbstractCard c, final AbstractMonster m) {
		int index = 1 + p().hand.group.indexOf(c);
		if (this.inited && (this.increasing ^ this.counter <= index)) {
			p().gainGold(1);
			this.show();
		}
		if (this.counter != -1 && !this.inited) {
			this.inited = true;
		}
		this.increasing = this.counter <= index;
		this.counter = index;
		this.updateHandGlow();
    }
	
	public void onRefreshHand() {
		if (color == null)
			color = AcrobatUp.setColorIfNull(this::initGlowColor);
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		if (!this.inCombat() || !this.inited)
			return;
		for (AbstractCard c : p().hand.group) {
			int index = 1 + p().hand.group.indexOf(c);
			if ((increasing ^ counter <= index) && c.hasEnoughEnergy() && c.cardPlayable(randomMonster())) {
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
		this.inited = false;
    }

	public void onVictory() {
		this.stopPulse();
		this.counter = -1;
		this.inited = false;
	}
	
	public boolean canSpawn() {
		return (Settings.isEndless) || (AbstractDungeon.floorNum <= 48);
	}

}