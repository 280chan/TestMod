package cards.curse;

import cards.AbstractTestCurseCard;
import relics.Sins;

import java.util.stream.Stream;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster.Intent;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.UIStrings;

public class Envy extends AbstractTestCurseCard {
	private static final UIStrings UI = INSTANCE.uiString();
	public static final String ID = "Envy";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;

	public static final Intent[] ATTACK = {Intent.ATTACK, Intent.ATTACK_BUFF, Intent.ATTACK_DEBUFF,
			Intent.ATTACK_DEFEND};
	public static final Intent[] DEFEND_DEBUFF = {Intent.ATTACK_DEBUFF, Intent.ATTACK_DEFEND, Intent.DEBUFF,
			Intent.DEFEND, Intent.DEFEND_BUFF, Intent.DEFEND_DEBUFF, Intent.STRONG_DEBUFF};
	public static final Intent[] BUFF = {Intent.ATTACK_BUFF, Intent.BUFF, Intent.DEFEND_BUFF};

	public Envy() {
		super(ID, NAME, DESCRIPTION);
		this.exhaust = true;
	}

	public boolean canPlay(AbstractCard card) {
		if (this.hasPrudence())
			return true;
		if (card.type == CardType.CURSE || card.type == CardType.STATUS) {
			card.cantUseMessage = UI.TEXT[0];
			return false;
		}
		if (card.type == CardType.ATTACK && hasIntentNot(ATTACK)) {
			card.cantUseMessage = UI.TEXT[1];
			return false;
		}
		if (card.type == CardType.SKILL && hasIntentNot(DEFEND_DEBUFF)) {
			card.cantUseMessage = UI.TEXT[2];
			return false;
		}
		if (card.type == CardType.POWER && hasIntentNot(BUFF)) {
			card.cantUseMessage = UI.TEXT[3];
			return false;
		}
		return true;
	}
	
	private boolean hasIntentNot(Intent[] intent) {
		return !AbstractDungeon.getMonsters().monsters.stream().allMatch(new IntentChecker(intent)::check);
	}
	
	private static class IntentChecker {
		AbstractMonster m;
		Intent[] i;
		IntentChecker(AbstractMonster m) {
			this.m = m;
		}
		IntentChecker(Intent[] i) {
			this.i = i;
		}
		boolean check(Intent i) {
			return this.m.intent == i || this.m.isDead || this.m.halfDead;
		}
		boolean check(AbstractMonster m) {
			return hasIntent(m, this.i);
		}
	}
	
	private static boolean hasIntent(AbstractMonster m, Intent[] intent) {
		return Stream.of(intent).anyMatch(new IntentChecker(m)::check);
	}
	
	public AbstractCard makeCopy() {
		if (Sins.isObtained())
			return new Envy();
		return Sins.copyCurse();
	}

}