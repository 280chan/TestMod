
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;
import powers.PulseDistributorPower;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.*;

public class PulseDistributor extends CustomCard {
	public static final String ID = "PulseDistributor";
	public static final String NAME = "脉冲分配器";
	public static final String IMG = TestMod.cardIMGPath("relic1");
	public static final String DESCRIPTION = "受到n点非失去生命的伤害时，不直接损失生命值，而是在此回合开始的n+1个回合内每次敌人回合结束时失去1点生命。";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
	private static final String[] DESCRIPTIONS = {"受到n点非失去生命的伤害时，不直接损失生命值，而是在此回合开始的n", "个回合内每次敌人回合结束时失去1点生命。"};
	private static final int COST = 3;//卡牌费用
	private static final int INITIAL_MAGIC = 1;

	public PulseDistributor() {
		super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.RARE, CardTarget.SELF);
		this.baseMagicNumber = INITIAL_MAGIC;// 特殊值，一般用来叠BUFF层数。和下一行连用。
		this.magicNumber = this.baseMagicNumber;
		this.updatedDescription();
	}

	private void updatedDescription() {
		String temp = DESCRIPTIONS[0];
		if (this.magicNumber != 0) {
			if (this.magicNumber > 0) {
				temp += "+";
			}
			temp += this.magicNumber;
		}
		this.rawDescription = temp + DESCRIPTIONS[1];
		this.initializeDescription();
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		if (p.hasPower("PulseDistributorPower")) {
			PulseDistributorPower power = (PulseDistributorPower)p.getPower("PulseDistributorPower");
			if (power.magic > this.magicNumber) {
				AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(p, p, "PulseDistributorPower"));
				AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new PulseDistributorPower(p, this.magicNumber, power.DAMAGES)));
			}
		} else {
			AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new PulseDistributorPower(p, this.magicNumber)));
		}
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();// 改名，其实就是多个+
			this.upgradeMagicNumber(-1);// 升级增加的特殊常量MagicNumber
			this.updatedDescription();
		}
	}// 升级后额外增加（括号内的）值，以及升级后的各种改变
}