
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.SubstituteBySubterfugeAction;

import com.megacrit.cardcrawl.dungeons.*;

public class SubstituteBySubterfuge extends CustomCard {
	public static final String ID = "SubstituteBySubterfuge";
	public static final String NAME = "偷天换日";
	public static final String IMG = TestMod.cardIMGPath("relic1");
	public static final String DESCRIPTION = "获得 [R] 。从你的抽牌堆中选择丢弃任意张牌。同时将你的弃牌堆洗牌后放入你的抽牌堆。";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
	public static final String[] DESCRIPTIONS = {"获得", "。从你的抽牌堆中选择丢弃任意张牌。同时将你的弃牌堆洗牌后放入你的抽牌堆。"};
	private static final String[] E = {" [R] ", " [G] ", " [B] ", " [P] "};
	private static final int COST = 0;// 卡牌费用

	public SubstituteBySubterfuge() {
		super(TestMod.makeID(ID), NAME, IMG, COST, getDescription() + " 消耗 。", CardType.SKILL, CardColor.COLORLESS, CardRarity.RARE, CardTarget.NONE);
		this.exhaust = true;// 消耗属性，false不消耗，true消耗。可在该类里调用改变。不消耗就可以赋值为false或者删掉这一行
	}

	public static String getDescription() {
		String temp = DESCRIPTIONS[0];
		String e = E[0];
		if (AbstractDungeon.player != null) {
			switch (AbstractDungeon.player.chosenClass) {
			case WATCHER:
				e = E[3];
				break;
			case DEFECT:
				e = E[2];
				break;
			case THE_SILENT:
				e = E[1];
			default:
			}
		}
		temp += e;
		temp += DESCRIPTIONS[1];
		return temp;
	}
	
	public void use(final AbstractPlayer p, final AbstractMonster m) {
		AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
		AbstractDungeon.actionManager.addToBottom(new SubstituteBySubterfugeAction(p));
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();// 改名，其实就是多个+
			this.rawDescription = getDescription();
			this.initializeDescription();
			this.exhaust = false;
		}
	}// 升级后额外增加（括号内的）值，以及升级后的各种改变
}