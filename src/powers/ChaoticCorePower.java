package powers;

import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.actions.defect.IncreaseMaxOrbAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

public class ChaoticCorePower extends AbstractTestPower {
	public static final String POWER_ID = "ChaoticCorePower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	private static final int ORB_SLOT = 1;

	public ChaoticCorePower(AbstractCreature owner, int amount) {
		super(POWER_ID);
		this.name = NAME;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}

	public void updateDescription() {
		this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
	}

	public void onAttack(final DamageInfo info, final int damage, final AbstractCreature target) {
		if (info.type == DamageType.NORMAL && damage > 0) {
			this.channel();
		}
	}

	private void channel() {
		for (int i = 0; i < this.amount; i++) {
			this.addToBot(new ChannelAction(AbstractOrb.getRandomOrb(true)));
		}
	}
	
	private int countAddOrbSlotAction() {
		return (int) AbstractDungeon.actionManager.actions.stream().filter(a -> {
			return a instanceof IncreaseMaxOrbAction;
		}).count();
	}
	
	public int onAttacked(final DamageInfo info, final int damage) {
		if (damage > 0) {
			if (countAddOrbSlotAction() + AbstractDungeon.player.maxOrbs >= 10) {
				this.channel();
			} else {
				this.addToBot(new IncreaseMaxOrbAction(ORB_SLOT));
			}
		}
		return damage;
	}

}
