package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import relics.IndustrialRevolution;
import utils.MiscMethods;

public class InorganicPower extends AbstractTestPower implements OnReceivePowerPower, MiscMethods {
	public static final String POWER_ID = "InorganicPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	private static boolean check(AbstractCreature m) {
		return IndustrialRevolution.LIST.contains(m);
	}

	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof InorganicPower);
	}
	
	public InorganicPower(AbstractCreature owner) {
		super(POWER_ID);
		this.name = NAME;
		this.owner = owner;
		this.amount = -1;
		updateDescription();
		this.type = PowerType.DEBUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.owner.name + DESCRIPTIONS[1];
	}
	
	public void stackPower(int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	@Override
	public boolean onReceivePower(AbstractPower p, AbstractCreature t, AbstractCreature s) {
		return !(check(s) && t.equals(this.owner) && s.equals(this.owner) && p.type == PowerType.BUFF)
				|| p.ID.equals("Mode Shift");
	}
	
	public void onRemove() {
		this.addTmpActionToTop(() -> {
			if (!hasThis(this.owner))
				this.owner.powers.add(new InorganicPower(this.owner));
		});
	}
    
}
