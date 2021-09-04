package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import relics.Justice;

public class JusticePower extends AbstractTestPower implements OnReceivePowerPower, InvisiblePower {
	public static final String POWER_ID = "JusticePower";
	private Justice j;
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> {return p instanceof JusticePower;});
	}
	
	public JusticePower(AbstractCreature owner, Justice j) {
		super(POWER_ID);
		this.j = j;
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
	public boolean onReceivePower(AbstractPower p, AbstractCreature t, AbstractCreature source) {
		if (t.isPlayer && p.type == PowerType.DEBUFF) {
    		this.j.show();
    		this.addToTop(new ApplyPowerAction(t, t, new StrengthPower(t, 1), 1));
    	}
		return true;
	}

	public void onRemove() {
		this.addToTop(new AbstractGameAction() {
			@Override
			public void update() {
				this.isDone = true;
				if (!hasThis(JusticePower.this.owner))
					JusticePower.this.owner.powers.add(new JusticePower(JusticePower.this.owner, JusticePower.this.j));
			}
		});
	}
	
}
