package cards.curse;

import cards.AbstractTestCurseCard;
import relics.Sins;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster.Intent;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;

public class Envy extends AbstractTestCurseCard {
	public static final String ID = "Envy";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;

	public static final Intent[] ATTACK = {Intent.ATTACK, Intent.ATTACK_BUFF, Intent.ATTACK_DEBUFF, Intent.ATTACK_DEFEND};
	public static final Intent[] DEFEND_DEBUFF = {Intent.ATTACK_DEBUFF, Intent.ATTACK_DEFEND, Intent.DEBUFF, Intent.DEFEND, Intent.DEFEND_BUFF, Intent.DEFEND_DEBUFF, Intent.STRONG_DEBUFF};
	public static final Intent[] BUFF = {Intent.ATTACK_BUFF, Intent.BUFF, Intent.DEFEND_BUFF};

	public Envy() {
		super(ID, NAME, DESCRIPTION);
	}

	public boolean canPlay(AbstractCard card) {
		if (this.hasPrudence())
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
	}

}