package testmod.cards.colorless;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import testmod.cards.AbstractTestCard;

import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

public class PainDetonator extends AbstractTestCard {

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.atb(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AttackEffect.SLASH_HEAVY));
	}
	
	public void calculateCardDamage(AbstractMonster m) {
		this.baseDamage = (int) ((m.maxHealth - m.currentHealth) / 100f * this.magicNumber);
		super.calculateCardDamage(m);
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(10);
		}
	}
}