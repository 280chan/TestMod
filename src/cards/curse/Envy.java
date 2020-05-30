package cards.curse;

import basemod.abstracts.*;
import mymod.TestMod;
import relics.Prudence;
import relics.Sins;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster.Intent;
import com.megacrit.cardcrawl.dungeons.*;

public class Envy extends CustomCard {
	public static final String ID = "Envy";
	public static final String NAME = "嫉妒";
    public static final String IMG = TestMod.cardIMGPath("relic1");
	public static final String DESCRIPTION = " 不能被打出 。在手牌时，不能打出 状态 和 诅咒 牌。有敌人的意图不是攻击/debuff或防御/强化时，不能打出 攻击 /技能/ 能力 牌。";//卡牌说明。说明里面【 !D! 】、【 !B! 】、【 !M! 】分别指代this.baseBlock、this.baseDamage、this.baseMagic。使用时记得的注意前后空格，关键字前后也要加空格
	private static final int COST = -2;//卡牌费用

	public static final Intent[] ATTACK = {Intent.ATTACK, Intent.ATTACK_BUFF, Intent.ATTACK_DEBUFF, Intent.ATTACK_DEFEND};
	public static final Intent[] DEFEND_DEBUFF = {Intent.ATTACK_DEBUFF, Intent.ATTACK_DEFEND, Intent.DEBUFF, Intent.DEFEND, Intent.DEFEND_BUFF, Intent.DEFEND_DEBUFF, Intent.STRONG_DEBUFF};
	public static final Intent[] BUFF = {Intent.ATTACK_BUFF, Intent.BUFF, Intent.DEFEND_BUFF};

	public Envy() {
		super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.CURSE, CardColor.CURSE, CardRarity.SPECIAL, CardTarget.NONE);
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
	}

	public boolean canUse(AbstractPlayer p, AbstractMonster m) {
		return p.hasRelic(Prudence.ID) || p.hasRelic("Blue Candle");
	}

	public boolean canPlay(AbstractCard card) {
		if (AbstractDungeon.player.hasRelic(Prudence.ID))
			return true;
		if (card.type == CardType.CURSE || card.type == CardType.STATUS) {
			card.cantUseMessage = "嫉妒:我无法打出 #r状态牌 或 #r诅咒牌 ";
			return false;
		}
		if (card.type == CardType.ATTACK && hasIntentNot(ATTACK)) {
			card.cantUseMessage = "嫉妒:我无法打出 #r攻击牌 ";
			return false;
		}
		if (card.type == CardType.SKILL && hasIntentNot(DEFEND_DEBUFF)) {
			card.cantUseMessage = "嫉妒:我无法打出 #r技能牌 ";
			return false;
		}
		if (card.type == CardType.POWER && hasIntentNot(BUFF)) {
			card.cantUseMessage = "嫉妒:我无法打出 #r能力牌 ";
			return false;
		}
		return true;
	}
	
	private boolean hasIntentNot(Intent[] intent) {
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (hasIntent(m, intent)) {
				continue;	// 如果该敌人有intent意图，跳过
			}
			return true;	// 该敌人没有intent意图，返回真
		}
		return false;	// 所有敌人均没有intent意图，返回假
	}
	
	private boolean hasIntent(AbstractMonster m, Intent[] intent) {
		for (Intent i : intent) {
			if (m.intent == i || m.isDead || m.halfDead) {
				return true;
			}
		}
		return false;
	}
	
	public AbstractCard makeCopy() {
		if (Sins.isObtained())
			return new Envy();
		return Sins.copyCurse();
	}// 复制卡牌后复制的卡，如果卡组里有复制卡牌的卡每张卡都要有这个

	public void upgrade() {
	}// 升级后额外增加（括号内的）值，以及升级后的各种改变
}