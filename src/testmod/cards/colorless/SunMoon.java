package testmod.cards.colorless;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.AttackDamageRandomEnemyAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import testmod.cards.AbstractTestCard;

public class SunMoon extends AbstractTestCard {

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.getIdenticalList(AttackEffect.SLASH_HORIZONTAL, this.getMonth())
				.forEach(e -> this.addToBot(new AttackDamageRandomEnemyAction(this, e)));
	}

	public void calculateCardDamage(AbstractMonster m) {
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