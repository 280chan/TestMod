package testmod.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import testmod.relics.Justice;

public class JusticePower extends AbstractTestPower implements OnReceivePowerPower, InvisiblePower {
	public static final String POWER_ID = "JusticePower";
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof JusticePower);
	}
	
	public JusticePower(AbstractCreature owner) {
		super(POWER_ID);
		this.name = POWER_ID;
		this.owner = owner;
		this.amount = -1;
		updateDescription();
		this.type = PowerType.BUFF;
		this.addMap(p -> new JusticePower(p.owner));
	}
	
	public void updateDescription() {
		 this.description = "";
	}

	public void stackPower(int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	@Override
	public boolean onReceivePower(AbstractPower p, AbstractCreature t, AbstractCreature source) {
		if (t.isPlayer && p.type == PowerType.DEBUFF)
			relicStream(Justice.class).peek(r -> r.show()).forEach(r -> att(apply(t, new StrengthPower(t, 1))));
		return true;
	}
	
}
