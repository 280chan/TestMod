package powers;

import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BloodBladePower extends AbstractTestPower {
	public static final String POWER_ID = "BloodBladePower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	private boolean upgraded = false;
	private float bonus = 0;
	
	public BloodBladePower(AbstractCreature owner, boolean upgraded) {
		super(POWER_ID);
		this.name = NAME;
		if (upgraded)
			this.name += "+";
		this.ID += upgraded;
		this.owner = owner;
		this.amount = -1;
		this.upgraded = upgraded;
		this.onFirstGain();
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0];
		 this.description += upgraded ? DESCRIPTIONS[2] : DESCRIPTIONS[1];
		 this.description += DESCRIPTIONS[3];
		 if (this.bonus > 0) {
			 double tmp = (((int)(this.bonus * 10000 + 0.5)) / 100.0);
			 this.description += DESCRIPTIONS[4] + (tmp) + "% ";
		 }
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
		this.amount = -1;
	}
	
	private static class PowerChecker {
		boolean flag;
		PowerChecker(boolean upgraded) {
			this.flag = upgraded;
		}
		boolean check(AbstractPower p) {
			return p instanceof BloodBladePower && this.flag == ((BloodBladePower) p).upgraded;
		}
	}
	
	public static boolean hasThis(boolean upgraded) {
		return AbstractDungeon.player.powers.stream().anyMatch(new PowerChecker(upgraded)::check);
	}
	
	public static BloodBladePower getThis(boolean upgraded) {
		return (BloodBladePower) AbstractDungeon.player.powers.stream().filter(new PowerChecker(upgraded)::check)
				.findAny().orElse(null);
	}
	
    public float atDamageGive(final float damage, final DamageType type) {
    	return type != DamageType.HP_LOSS ? damage * (1 + this.bonus) : damage;
    }
    
    public void onFirstGain() {
    	this.increaseRate(1 - (1.0f * owner.currentHealth / owner.maxHealth));
    	this.flash();
    }
    
    private void increaseRate(float rate) {
    	this.bonus = this.upgraded ? (1 + this.bonus) * (1 + rate) - 1 : this.bonus + rate;
    	this.updateDescription();
    }
    
    public int onLoseHp(final int damage) {
    	if (damage > 0) {
        	this.increaseRate(damage * 1.0f / owner.maxHealth);
    	}
        return damage;
    }
    
}
