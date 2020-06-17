package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import relics.IndustrialRevolution;

public class IndustrialRevolutionPower extends AbstractTestPower implements OnReceivePowerPower, InvisiblePower {
	public static final String POWER_ID = "IndustrialRevolutionPower";

	private static boolean check(AbstractCreature m) {
		return IndustrialRevolution.LIST.contains(m);
	}
	
	public static boolean hasThis(AbstractCreature owner) {
		for (AbstractPower p : owner.powers)
			if (p instanceof IndustrialRevolutionPower)
				return true;
		return false;
	}
	
	public IndustrialRevolutionPower(AbstractCreature owner) {
		super(POWER_ID);
		this.name = POWER_ID;
		this.owner = owner;
		this.amount = -1;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = "";
	}

	public void stackPower(int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	@Override
	public boolean onReceivePower(AbstractPower p, AbstractCreature t, AbstractCreature s) {
		if (t.isPlayer && check(s) && p.type == PowerType.DEBUFF)
			return false;
		return true;
	}
    
}
