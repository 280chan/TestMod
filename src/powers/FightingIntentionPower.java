package powers;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class FightingIntentionPower extends AbstractTestPower {
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

	public void atStartOfTurnPostDraw() {
		ArrayList<AbstractMonster> list = new ArrayList<AbstractMonster>();
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
			if (this.hasAttackIntent(m))
				list.add(m);
		int count = list.size() * this.amount;
		if (count <= 0)
			return;
		for (AbstractMonster m : list)
			this.addToBot(new ApplyPowerAction(m, this.owner, new StrengthPower(m, -count), -count));
		this.addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, count), count));
		this.flash();
	}

}
