package powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.actions.defect.IncreaseMaxOrbAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;

public class ChaoticCorePower extends AbstractPower {
	public static final String POWER_ID = "ChaoticCorePower";
	public static final String NAME = "混沌原核";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = { "每受到一次伤害，获得 #b1 个充能球栏位，已满则改为触发下一个效果。每造成一次 #y攻击 伤害， 生成  #b", " 个随机充能球。" };// 需要调用变量的文本描叙，例如力量（Strength）、敏捷（Dexterity）等。
	private static final int ORB_SLOT = 1;

	public ChaoticCorePower(AbstractCreature owner, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.BUFF;
	}

	public void updateDescription() {
		this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
	}

	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
		this.amount += stackAmount;
	}

	public void onAttack(final DamageInfo info, final int damage, final AbstractCreature target) {
		if (info.type == DamageType.NORMAL && damage > 0) {
			this.channel();
		}
	}

	private void channel() {
		for (int i = 0; i < this.amount; i++) {
			AbstractDungeon.actionManager.addToBottom(new ChannelAction(AbstractOrb.getRandomOrb(true)));
		}
	}
	
	private int countAddOrbSlotAction() {
		int retVal = 0;
		for (AbstractGameAction a : AbstractDungeon.actionManager.actions) {
			if (a instanceof IncreaseMaxOrbAction) {
				retVal++;
			}
		}
		return retVal;
	}
	
	public int onAttacked(final DamageInfo info, final int damage) {
		if (damage > 0) {
			if (countAddOrbSlotAction() + AbstractDungeon.player.maxOrbs >= 10) {
				this.channel();
			} else {
				AbstractDungeon.actionManager.addToBottom(new IncreaseMaxOrbAction(ORB_SLOT));
			}
		}
		return damage;
	}

}
