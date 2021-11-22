package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import relics.OneHitWonder;
import utils.MiscMethods;

public class OneHitWonderDebuffPower extends AbstractTestPower implements InvisiblePower, MiscMethods {
	public static final String POWER_ID = "OneHitWonderDebuffPower";
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof OneHitWonderDebuffPower);
	}
	
	public OneHitWonderDebuffPower(AbstractCreature owner) {
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
	
	private boolean checkPlayerHealth() {
		return p().currentHealth == 1;
	}
	
	private float f(float multiplier, float input) {
		return (float) (Math.pow(multiplier, this.relicStream(OneHitWonder.class).count()) * input);
	}
	
	public int onAttacked(DamageInfo info, int damage) {
		return checkPlayerHealth() && info.type != DamageType.NORMAL ? (int) f(1.5f, damage) : damage;
	}
	
	public float atDamageFinalGive(float damage, DamageType type) {
		return checkPlayerHealth() ? f(0.5f, damage) : damage;
	}
    
	public float atDamageFinalReceive(float damage, DamageType type) {
		return checkPlayerHealth() && type == DamageType.NORMAL ? f(1.5f, damage) : damage;
	}

	public void onRemove() {
		this.addTmpActionToTop(() -> {
			if (!hasThis(this.owner))
				this.owner.powers.add(new OneHitWonderDebuffPower(this.owner));
		});
	}
    
}
