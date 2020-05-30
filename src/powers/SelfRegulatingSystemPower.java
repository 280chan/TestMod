package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

public class SelfRegulatingSystemPower extends AbstractPower implements OnReceivePowerPower {
	public static final String POWER_ID = "SelfRegulatingSystemPower";//能力的ID，判断有无能力、能力层数时填写该Id而不是类名。
	public static final String NAME = "自我调节系统";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = {"每回合获得的首个增益状态层数增加 #b"," ，首个减益状态层数减少 #b"," 。"};
	
	private boolean buffed = false;
	private boolean debuffed = false;
	
	public SelfRegulatingSystemPower(AbstractCreature owner, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];//不需要调用变量的文本更新方式。
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
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
