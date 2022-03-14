package testmod.powers;

import java.util.ArrayList;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

import testmod.utils.MiscMethods;

public class FightingIntentionPower extends AbstractTestPower implements MiscMethods {
	public static final String POWER_ID = "FightingIntentionPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	
	public FightingIntentionPower(AbstractCreature owner, int amount) {
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

	private boolean hasAttackIntent(AbstractMonster m) {
		if (m.halfDead || m.isDead || m.isDying || m.isEscaping || m.escaped)
			return false;
		switch (m.intent) {
		case ATTACK:
		case ATTACK_BUFF:
		case ATTACK_DEBUFF:
		case ATTACK_DEFEND:
			return true;
		default:
			return false;
		}
	}
	
	private class PowerApplier {
		int amount;
		PowerApplier(int amount) {
			this.amount = amount;
		}
		void apply(AbstractCreature c) {
			FightingIntentionPower.this.addToBot(
					new ApplyPowerAction(c, FightingIntentionPower.this.owner, new StrengthPower(c, amount), amount));
		}
	}

	public void atStartOfTurnPostDraw() {
		ArrayList<AbstractMonster> list = AbstractDungeon.getMonsters().monsters.stream().filter(this::hasAttackIntent)
				.collect(this.toArrayList());
		int count = list.size() * this.amount;
		if (count <= 0)
			return;
		list.forEach(new PowerApplier(-count)::apply);
		new PowerApplier(count).apply(this.owner);
		this.flash();
	}

}
