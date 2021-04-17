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

public class NegativeEmotionEnhancer extends AbstractTestRelic {
	public static final String ID = "NegativeEmotionEnhancer";
	
	public NegativeEmotionEnhancer() {
		super(ID, RelicTier.BOSS, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}

	public void onEquip() {
		AbstractDungeon.player.energy.energyMaster++;
    }
	
	public void onUnequip() {
		AbstractDungeon.player.energy.energyMaster--;
    }
	
	public void atPreBattle() {
		this.counter = 0;
	}
	
	public void atTurnStart() {
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
		AbstractDungeon.player.increaseMaxHp(1, true);
	}
	
	public void onMonsterDeath(AbstractMonster m) {
		AbstractDungeon.player.heal(1);
	}
	
}