package powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import mymod.TestMod;

public class FightingIntentionPower extends AbstractPower {
	public static final String POWER_ID = "FightingIntentionPower";
	public static final String NAME = "战意";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = { "回合开始时，每个攻击意图的敌人使你获得 #b", " 力量 ，使攻击意图的敌人失去 #b", " 力量 。" };

	public FightingIntentionPower(AbstractCreature owner, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.BUFF;
	}

	public void updateDescription() {
		this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
	}

	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
		this.amount += stackAmount;
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
		int count = 0;
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
			if (this.hasAttackIntent(m))
				count++;
		count *= this.amount;
		if (count <= 0)
			return;
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
			if (this.hasAttackIntent(m))
				AbstractDungeon.actionManager
						.addToBottom(new ApplyPowerAction(m, this.owner, new StrengthPower(m, -count), -count));
		AbstractDungeon.actionManager
				.addToBottom(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, count), count));
		this.flash();
	}

}
