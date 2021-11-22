package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class HolyLightProtectionPower extends AbstractTestPower implements InvisiblePower {
	public static final String POWER_ID = "HolyLightProtection";
	
	public static boolean notHasThis(AbstractCreature m) {
		return m.powers.stream().noneMatch(p -> p instanceof HolyLightProtectionPower);
	}
	
	public HolyLightProtectionPower(AbstractCreature m) {
		super(POWER_ID);
		this.owner = m;
		this.name = POWER_ID;
		updateDescription();
		this.type = PowerType.DEBUFF;
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

	public void onRemove() {
		this.addTmpActionToTop(() -> {
			if (notHasThis(owner))
				owner.powers.add(new HolyLightProtectionPower(owner));
		});
	}
	
}
