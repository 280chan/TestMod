package testmod.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class HolyLightProtectionPower extends AbstractTestPower implements InvisiblePower {
	public static final String POWER_ID = "HolyLightProtection";
	
	public HolyLightProtectionPower(AbstractCreature m) {
		this.owner = m;
		updateDescription();
		this.type = PowerType.DEBUFF;
		this.addMap(p -> new HolyLightProtectionPower(p.owner));
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	public float atDamageFinalGive(float damage, DamageInfo.DamageType type) {
		return 0;
	}
	
}
