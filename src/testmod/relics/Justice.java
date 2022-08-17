package testmod.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.OnReceivePowerRelic;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.AbstractPower.PowerType;

public class Justice extends AbstractTestRelic implements OnReceivePowerRelic {
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0] + (Loader.isModLoaded("RelicUpgradeLib") ? DESCRIPTIONS[1] : "");
	}

	@Override
	public boolean onReceivePower(AbstractPower p, AbstractCreature source) {
		if (this.isActive && p.type == PowerType.DEBUFF)
			relicStream(Justice.class).peek(r -> r.show()).forEach(r -> att(apply(p(), new StrengthPower(p(), 1))));
		return true;
	}
	
}