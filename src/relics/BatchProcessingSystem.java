package relics;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import mymod.TestMod;
import utils.MiscMethods;

public class BatchProcessingSystem extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "BatchProcessingSystem";
	
	private static Color color = null;
	
	public BatchProcessingSystem() {
		super(ID, RelicTier.BOSS, LandingSound.SOLID);
	}
	
	public String getUpdatedDescription() {
		if (AbstractDungeon.player != null) {
			return setDescription(AbstractDungeon.player.chosenClass);
		}
		return setDescription(null);
	}

	private String setDescription(PlayerClass c) {
		return this.setDescription(c, this.DESCRIPTIONS[0], this.DESCRIPTIONS[1], this.DESCRIPTIONS[2]);
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, setDescription(c)));
	    initializeTips();
	}
	
	public void onPlayCard(final AbstractCard c, final AbstractMonster m) {
		if (!this.isActive)
			return;
		if (c.isInAutoplay)
			return;
		if (this.counter == c.costForTurn) {
			this.show();
			this.addToBot(new GainEnergyAction(1));
		}
		this.counter = c.costForTurn;
		this.updateHandGlow();
	}
	
	public void onRefreshHand() {
		if (color == null)
			color = this.initGlowColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		if (AbstractDungeon.currMapNode == null)
			return;
		if (AbstractDungeon.currMapNode.room == null)
			return;
		if (AbstractDungeon.currMapNode.room.monsters == null)
			return;
		if (AbstractDungeon.currMapNode.room.monsters.areMonstersBasicallyDead())
			return;
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			if (c.costForTurn == this.counter && c.hasEnoughEnergy() && c.cardPlayable(AbstractDungeon.getRandomMonster())) {
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
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!this.isActive)
			return;
		AbstractDungeon.player.energy.energyMaster--;
		if (color == null)
			color = this.initGlowColor();
    }
	
	public void onUnequip() {
		if (!this.isActive)
			return;
		AbstractDungeon.player.energy.energyMaster++;
    }
	
	public void atTurnStart() {
		if (!this.isActive)
			return;
		this.counter = -1;
    }
	
	public void onVictory() {
		this.stopPulse();
	}
	
}