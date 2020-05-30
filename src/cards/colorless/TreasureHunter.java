
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.TreasureHuntAttackAction;

public class TreasureHunter extends CustomCard {
	public static final String ID = "TreasureHunter";
	public static final String NAME = "宝藏猎手";
	public static final String IMG = TestMod.cardIMGPath("relic1");
	public static final String DESCRIPTION = "造成 !D! 点伤害。从3张随机稀有牌中选择1张加入手牌。 消耗 。";
	private static final String[] DESCRIPTIONS = { "造成 !D! 点伤害。从3张随机稀有牌中选择1张加入手牌。", " 斩杀 ，同时将其加入牌组。", " 消耗 。" };
	private static final int COST = 2;// 卡牌费用
	private static final int ATTACK_DMG = 8;// 基础伤害值

	public TreasureHunter() {
		super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.RARE,
				CardTarget.ENEMY);
		this.baseDamage = ATTACK_DMG;

		this.exhaust = true;
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(
				new TreasureHuntAttackAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), this.upgraded));
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();// 改名，其实就是多个+
			this.rawDescription = DESCRIPTIONS[0] + DESCRIPTIONS[1] + DESCRIPTIONS[2];
			initializeDescription();
		}
	}// 升级后额外增加（括号内的）值，以及升级后的各种改变
}