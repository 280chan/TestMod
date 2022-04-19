package testmod.powers;

import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.actions.defect.IncreaseMaxOrbAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

public class ChaoticCorePower extends AbstractTestPower {
	private static final int ORB_SLOT = 1;

	public ChaoticCorePower(AbstractCreature owner, int amount) {
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}

	public void updateDescription() {
		this.description = desc(0) + this.amount + desc(1);
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
		return (int) AbstractDungeon.actionManager.actions.stream().filter(a -> a instanceof IncreaseMaxOrbAction)
				.count();
	}
	
	public int onAttacked(final DamageInfo info, final int damage) {
		if (damage > 0) {
			if (countAddOrbSlotAction() + p().maxOrbs >= 10) {
				this.channel();
			} else {
				this.addToBot(new IncreaseMaxOrbAction(ORB_SLOT));
			}
		}
		return damage;
	}

}
