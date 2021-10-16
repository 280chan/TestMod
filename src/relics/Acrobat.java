package relics;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Acrobat extends AbstractTestRelic {
	public static final String ID = "Acrobat";
	private static Color color = null;
	public int state = 0;
	
	public Acrobat() {
		super(ID, RelicTier.COMMON, LandingSound.CLINK);
		this.setTestTier(BAD);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void onPlayCard(final AbstractCard c, final AbstractMonster m) {
		int index = 1 + AbstractDungeon.player.hand.group.indexOf(c);
		int state = 0;
		if (this.counter == -1)
			this.counter = index;
		else
			state = this.counter <= index ? 1 : -1;
		this.counter = index;
		if (this.state * state < 0) {
			AbstractDungeon.player.gainGold(1);
			this.show();
		}
		this.updateHandGlow();
		this.state = state;
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
			int index = 1 + AbstractDungeon.player.hand.group.indexOf(c);
			int state = 0;
			if (this.counter == -1)
				return;
			else if (this.counter <= index)
				state = 1;
			else
				state = -1;
			if (this.state * state < 0 && c.hasEnoughEnergy() && c.cardPlayable(AbstractDungeon.getRandomMonster())) {
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