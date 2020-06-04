package relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

import mymod.TestMod;
import utils.MiscMethods;

public class NegativeEmotionEnhancer extends MyRelic implements MiscMethods {
	public static final String ID = "NegativeEmotionEnhancer";
	
	public NegativeEmotionEnhancer() {
		super(ID, RelicTier.BOSS, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		if (AbstractDungeon.player != null) {
			return setDescription(AbstractDungeon.player.chosenClass);
		}
		return setDescription(null);
	}
	
	private String setDescription(PlayerClass c) {
		return setDescription(c, DESCRIPTIONS);
	}
	
	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, setDescription(c)));
	    initializeTips();
	}

	public void onEquip() {
		TestMod.setActivity(this);
		if (!this.isActive)
			return;
		AbstractDungeon.player.energy.energyMaster++;
    }
	
	public void onUnequip() {
		if (!this.isActive)
			return;
		AbstractDungeon.player.energy.energyMaster--;
    }
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		this.counter = 0;
	}
	
	public void atTurnStart() {
		if (!this.isActive)
			return;
		this.counter++;
		AbstractPlayer p = AbstractDungeon.player;
		switch (this.counter % 3) {
		case 1:
			this.addToBot(new ApplyPowerAction(p, p, new FrailPower(p, 1, false), 1));
			break;
		case 2:
			this.addToBot(new ApplyPowerAction(p, p, new WeakPower(p, 1, false), 1));
			break;
		case 0:
			this.addToBot(new ApplyPowerAction(p, p, new VulnerablePower(p, 1, false), 1));
			break;
		}
    }

	public void onVictory() {
		if (!this.isActive)
			return;
		AbstractDungeon.player.increaseMaxHp(1, true);
	}
	
	public void onMonsterDeath(AbstractMonster m) {
		AbstractDungeon.player.heal(1);
	}
	
}