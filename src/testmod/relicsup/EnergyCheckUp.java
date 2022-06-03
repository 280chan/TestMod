package testmod.relicsup;

import java.util.stream.IntStream;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class EnergyCheckUp extends AbstractUpgradedRelic implements ClickableRelic {
	private boolean newTurn = false;
	
	private int max() {
		return p().energy.energyMaster;
	}
	
	private static int key() {
		if (Loader.isModLoaded("RelicUpgradeLib")) {
			return IntStream.of(AllUpgradeRelic.MultiKey.KEY).sum() + 1;
		}
		int i = 1;
		if (Settings.hasRubyKey)
			i++;
		if (Settings.hasEmeraldKey)
			i++;
		if (Settings.hasSapphireKey)
			i++;
		return i;
	}
	
	public EnergyCheckUp() {
		super(RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public void atPreBattle() {
		this.counter = 0;
    }
	
	public void onVictory() {
		if (this.counter > p().currentHealth) {
			p().decreaseMaxHealth(Math.max(1, p().maxHealth / 10));
			this.addEnergy();
		} else if (this.counter > 0) {
			p().damage(new DamageInfo(null, this.counter, DamageType.HP_LOSS));
		}
		this.counter = -1;
	}
	
	public void onEnergyRecharge() {
		if (this.newTurn) {
			this.newTurn = false;
			if (this.counter > 0)
				this.repayEnergy(Math.min(Math.min(this.counter, this.max() * key()), EnergyPanel.totalCount * key()));
		}
	}
	
	private void repayEnergy(int amount) {
		this.counter -= amount;
		EnergyPanel.useEnergy(1 + (amount - 1) / key());
	}
	
	public void onPlayerEndTurn() {
		newTurn = true;
    }

	@Override
	public void onRightClick() {
		if (newTurn)
			return;
		this.addToBot(new GainEnergyAction(1));
		this.show();
		this.counter++;
	}
	
}