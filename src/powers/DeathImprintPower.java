package powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DeathImprintPower extends AbstractTestPower {
	public static final String POWER_ID = "DeathImprintPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> {return p instanceof DeathImprintPower;});
	}
	
	public static AbstractPower getThis(AbstractCreature owner) {
		for (AbstractPower p : owner.powers)
			if (p instanceof DeathImprintPower)
				return p;
		return null;
	}
	
	public DeathImprintPower(AbstractCreature owner, int amount) {
		super(POWER_ID);
		this.name = NAME;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.DEBUFF;
	}

	public void updateDescription() {
		this.description = DESCRIPTIONS[0] + this.owner.name + DESCRIPTIONS[1] + this.owner.name + DESCRIPTIONS[2] + this.amount + DESCRIPTIONS[3];
	}

	public int onAttacked(DamageInfo info, int damage) {
		if (damage > 0) {
			if (damage < 5 && AbstractDungeon.player.hasRelic("Boot") && info.type == DamageType.NORMAL) {
				this.amount += 5;
			} else {
				this.amount += damage;
			}
			this.updateDescription();
		}
		return damage;
	}

}
