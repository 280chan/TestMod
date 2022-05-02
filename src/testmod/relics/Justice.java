package testmod.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.OnReceivePowerRelic;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.AbstractPower.PowerType;

public class Justice extends AbstractTestRelic implements OnReceivePowerRelic {
	
	public Justice() {
		super(RelicTier.UNCOMMON, LandingSound.SOLID);
	}

	@Override
	public boolean onReceivePower(AbstractPower p, AbstractCreature source) {
		if (p.type == PowerType.DEBUFF)
			relicStream(Justice.class).peek(r -> r.show()).forEach(r -> att(apply(p(), new StrengthPower(p(), 1))));
		return true;
	}
	
}