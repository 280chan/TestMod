package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.relics.OnReceivePowerRelic;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import testmod.utils.CounterKeeper;
import testmod.utils.InfiniteUpgradeRelic;

import com.megacrit.cardcrawl.powers.AbstractPower.PowerType;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class JusticeUp extends AbstractUpgradedRelic implements OnReceivePowerRelic, CounterKeeper, InfiniteUpgradeRelic {
	
	public JusticeUp() {
		super(RelicTier.UNCOMMON, LandingSound.SOLID);
	}
	
	public void run(AbstractRelic r, AbstractUpgradedRelic u) {
		u.counter = r instanceof AbstractUpgradedRelic ? r.counter + 2 : 3;
		u.updateDescription();
	}
	
	public String getUpdatedDescription() {
		return this.isObtained ? DESCRIPTIONS[1] + this.counter + DESCRIPTIONS[2] : DESCRIPTIONS[0];
	}

	@Override
	public boolean onReceivePower(AbstractPower p, AbstractCreature source) {
		if (p.type == PowerType.DEBUFF) {
			this.show();
			this.att(apply(p(), new StrengthPower(p(), this.counter)));
		}
		return true;
	}
	
}