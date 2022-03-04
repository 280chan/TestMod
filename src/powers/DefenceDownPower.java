package powers;

import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;

import relics.AscensionHeart;

public class DefenceDownPower extends AbstractTestPower {
	public static final String POWER_ID = "DefenceDownPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof DefenceDownPower);
	}
	
	public DefenceDownPower(AbstractCreature owner, int amount) {
		super(POWER_ID);
		this.name = NAME;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.DEBUFF;
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + (single() ? amount : (dmgRate(100f) - 100)) + DESCRIPTIONS[1];
	}
	
	private float dmg(float input) {
		return input * (100 + this.amount) / 100;
	}
	
	private float dmgRate(float input) {
		return relicStream(AscensionHeart.class).map(r -> get(this::dmg)).reduce(t(), this::chain).apply(input);
	}
	
    public float atDamageReceive(float damage, DamageType damageType) {
        return dmgRate(damage);
    }
    
    private boolean single() {
		return relicStream(AscensionHeart.class).count() == 1;
	}
}
