package testmod.cards.colorless;

import testmod.actions.BloodthirstyAction;
import testmod.cards.AbstractTestCard;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

public class Bloodthirsty extends AbstractTestCard {
	public static final String ID = "Bloodthirsty";
	private static final int D_MGC = -2;
	private static final int UPGRADED_D_MGC = -1;
	private boolean used = false;

	public Bloodthirsty() {
		this.tags.add(CardTags.HEALING);
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.atb(new BloodthirstyAction(m, p, this.magicNumber));
		this.used = true;
	}
	
	public void calculateCardDamage(AbstractMonster m) {
		Long max = (long) m.maxHealth;
		Long rate = (long) this.magicNumber;
		Long amount = max * rate / 100L;
		if (amount > 2147483647L)
			amount = 2000000000L;
		this.misc = amount.intValue();
		if (this.misc > m.currentHealth)
			this.misc = m.currentHealth;
		super.calculateCardDamage(m);
		this.rawDescription = this.getDesc();
		this.initializeDescription();
	}

	private String getDesc() {
		return exDesc()[0] + ((this.misc > 0) ? "(" + this.misc + ")" : "") + exDesc()[1]
				+ exDesc()[(this.upgraded ? 3 : 2)] + exDesc()[4];
	}
	
	public void doublesMagicNumber() {
		this.upgradeMagicNumber(this.magicNumber > 1000000000 ? 2000000000 - this.magicNumber : this.magicNumber);
	}
	
	public void triggerOnCardPlayed(AbstractCard c) {
		if (!(c instanceof Bloodthirsty) && this.used) {
			if (!this.upgraded && this.magicNumber + D_MGC > 0) {
				this.upgradeMagicNumber(D_MGC);
			} else if (this.magicNumber + UPGRADED_D_MGC > 0) {
				this.upgradeMagicNumber(UPGRADED_D_MGC);
			}
		}
	}
	
	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(2);
			this.upDesc();
		}
	}

}