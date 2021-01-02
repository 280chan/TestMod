
package cards.colorless;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.AttackDamageRandomEnemyAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import cards.AbstractTestCard;
import utils.MiscMethods;

import com.megacrit.cardcrawl.localization.CardStrings;

public class SunMoon extends AbstractTestCard implements MiscMethods {
	public static final String ID = "SunMoon";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final int COST = 2;

	public SunMoon() {
		super(ID, NAME, COST, DESCRIPTION, CardType.ATTACK, CardRarity.RARE, CardTarget.ALL_ENEMY);
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		for (int i = 0; i < this.misc; i++) {
			this.addToBot(new AttackDamageRandomEnemyAction(this, AttackEffect.SLASH_HORIZONTAL));
		}
	}

	public void calculateCardDamage(AbstractMonster m) {
		this.misc = this.getMonth();
		this.baseDamage = this.getDate();
		super.calculateCardDamage(m);
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeBaseCost(1);
		}
	}
}