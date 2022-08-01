package testmod.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.OnReceivePowerRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.AbstractPower.PowerType;

import com.megacrit.cardcrawl.powers.WraithFormPower;

import testmod.relicsup.ReverseUp;

public class Reverse extends AbstractTestRelic implements OnReceivePowerRelic {
	
	public Reverse() {
		super(RelicTier.UNCOMMON, LandingSound.MAGICAL);
	}

	@Override
	public boolean onReceivePower(AbstractPower p, AbstractCreature source) {
		if (ReverseUp.b() > 0)
			return true;
		if (this.isActive && p.amount < 0 && (p.canGoNegative || p.amount != -1)) {
			int delta = (int) (-p.amount * relicStream(Reverse.class).peek(r -> r.show()).count());
			p.stackPower(delta - p.amount);
			if (p.amount < 0) {
				p.amount = delta;
			}
			p.updateDescription();
		}
		return true;
	}
	
	@Override
	public int onReceivePowerStacks(AbstractPower p, AbstractCreature source, int amount) {
		if (ReverseUp.b() > 0)
			return amount;
		if (amount < 0 && (p.canGoNegative || amount != -1))
			amount = -amount;
		return this.isActive && amount < 0 && (p.canGoNegative || amount != -1) ? ReverseUp.a() * amount : amount;
	}
	
	@SpirePatch(clz = WraithFormPower.class, method = "stackPower")
	public static class WraithFormPowerPatch {
		@SpirePostfixPatch
		public static void Postfix(WraithFormPower p, int stackAmount) {
			p.type = p.amount > 0 ? PowerType.BUFF : PowerType.DEBUFF;
		}
	}
	
}