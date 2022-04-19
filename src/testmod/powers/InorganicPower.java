package testmod.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

import testmod.relics.IndustrialRevolution;

public class InorganicPower extends AbstractTestPower implements OnReceivePowerPower {
	
	private static boolean check(AbstractCreature m) {
		return IndustrialRevolution.LIST.contains(m);
	}

	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof InorganicPower);
	}
	
	public InorganicPower(AbstractCreature owner) {
		this.owner = owner;
		this.amount = -1;
		updateDescription();
		this.type = PowerType.DEBUFF;
		this.addMap(p -> new InorganicPower(p.owner));
	}
	
	public void updateDescription() {
		 this.description = desc(0) + this.owner.name + desc(1);
	}
	
	public void stackPower(int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	@Override
	public boolean onReceivePower(AbstractPower p, AbstractCreature t, AbstractCreature s) {
		return !(check(s) && t.equals(this.owner) && s.equals(this.owner) && p.type == PowerType.BUFF)
				|| p.ID.equals("Mode Shift");
	}
    
}
