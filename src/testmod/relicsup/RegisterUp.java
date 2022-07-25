package testmod.relicsup;

import java.util.ArrayList;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.CombustPower;
import com.megacrit.cardcrawl.powers.AbstractPower.PowerType;
import testmod.relics.Register;

public class RegisterUp extends AbstractUpgradedRelic{
	private static final ArrayList<AbstractPower> POWERS = Register.POWERS;
	
	public void clear() {
		POWERS.clear();
	}
	
	public RegisterUp() {
		super(RelicTier.SHOP, LandingSound.CLINK);
	}
	
	private void updateInfo() {
		clear();
		POWERS.addAll(p().powers);
	}
	
	public static long amount() {
		return MISC.relicStream(RegisterUp.class).count();
	}
	
	public boolean filterAndChangeAmount(AbstractPower p) {
		if (p.type == PowerType.DEBUFF || p instanceof InvisiblePower)
			return false;
		long a = amount();
		p.amount = p instanceof CombustPower || p.amount == 0 || (Math.abs(p.amount) == 1 && a == 1) ? p.amount
				: p.amount < 0 ? Math.min((int) (p.amount * a) / 2, -1) : Math.max((int) (p.amount * a) / 2, 1);
		return true;
	}
	
	public static AbstractPower prepare(AbstractPower p) {
		return Register.prepare(p);
	}
	
	public void atBattleStart() {
		if (!isActive)
			return;
		POWERS.stream().filter(this::filterAndChangeAmount).forEach(p -> this.att(apply(p(), prepare(p))));
		if (!POWERS.isEmpty())
			this.show();
    }
	
	public void onVictory() {
		if (!isActive)
			return;
		updateInfo();
	}
}