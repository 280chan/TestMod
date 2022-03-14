package testmod.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class SelfRegulatingSystemPower extends AbstractTestPower implements OnReceivePowerPower {
	public static final String POWER_ID = "SelfRegulatingSystemPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	private boolean buffed = false;
	private boolean debuffed = false;
	
	public SelfRegulatingSystemPower(AbstractCreature owner, int amount) {
		super(POWER_ID);
		this.name = NAME;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
	}
	
    public void atStartOfTurn() {
    	this.debuffed = false;
    	this.buffed = false;
    }
    
    private void applyPower(AbstractCreature t, AbstractPower p, int factor) {
		if (!t.hasPower(p.ID)) {
			p.stackPower(factor);
			p.updateDescription();
		} else {
			t.getPower(p.ID).stackPower(factor);
			t.getPower(p.ID).updateDescription();
		}
    }
    
	@Override
	public boolean onReceivePower(AbstractPower p, AbstractCreature t, AbstractCreature source) {
		if (t.equals(this.owner)) {
			if (p.type == PowerType.BUFF) {
				if (buffed)
					return true;
				buffed = true;
				if (!p.canGoNegative) {
					if (p.amount <= 0)
						return true;
					this.applyPower(t, p, this.amount);
				} else {
					if (p.amount < 0) {
						this.applyPower(t, p, -this.amount);
					} else if (p.amount > 0) {
						this.applyPower(t, p, this.amount);
					} else {
						return true;
					}
				}
			} else {
				if (debuffed)
					return true;
				debuffed = true;
				if (!p.canGoNegative) {
					if (p.amount <= 0) {
						return p.amount + this.amount < 0;
					}
					if (p.amount - this.amount <= 0)
						return false;
					this.applyPower(t, p, -this.amount);
				} else {
					if (p.amount < 0) {
						if (p.amount + this.amount >= 0)
							return false;
						this.applyPower(t, p, this.amount);
					} else if (p.amount > 0) {
						if (p.amount - this.amount <= 0)
							return false;
						this.applyPower(t, p, -this.amount);
					} else {
						return false;
					}
				}
			}
		}
		return true;
	}

}
