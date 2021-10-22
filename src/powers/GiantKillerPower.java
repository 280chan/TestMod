package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import utils.MiscMethods;

public class GiantKillerPower extends AbstractTestPower implements InvisiblePower, MiscMethods {
	public static final String POWER_ID = "GiantKillerPower";
	
	public static boolean hasThis(AbstractCreature m) {
		return m.powers.stream().anyMatch(p -> p instanceof GiantKillerPower);
	}
	
	public static void addThis(AbstractCreature m) {
		m.powers.add(new GiantKillerPower(m));
	}
	
	public GiantKillerPower(AbstractCreature owner) {
		super(POWER_ID);
		this.name = POWER_ID;
		this.owner = owner;
		this.amount = -1;
		updateDescription();
		this.type = PowerType.DEBUFF;
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	public float rate(int b) {
		return this.owner.maxHealth * 1f / b;
	}
	
	public int onAttacked(DamageInfo info, int damage) {
		if (damage > 0) {
			if (p().maxHealth < 1)
				return 2000000000;
			return p().maxHealth < this.owner.maxHealth ? (int) (damage * rate(p().maxHealth)) : damage;
		}
		return damage;
	}
	
	public void onRemove() {
		this.addTmpActionToTop(() -> {
			if (!hasThis(this.owner))
				addThis(this.owner);
		});
	}

}
