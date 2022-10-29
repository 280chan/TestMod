
package testmod.cards.colorless;

import testmod.actions.BloodthirstyAction;
import testmod.cards.AbstractUpdatableCard;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

public class Bloodthirsty extends AbstractUpdatableCard {
	public static final String ID = "Bloodthirsty";
	private static final int D_MGC = -2;
	private static final int UPGRADED_D_MGC = -1;
	private boolean used = false;

	public Bloodthirsty() {
		this.tags.add(CardTags.HEALING);
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.preApplyPowers(p, m);
		super.applyPowers();
		this.atb(new BloodthirstyAction(m, p, this.magicNumber));
		this.used = true;
	}

	@Override
	public void preApplyPowers(AbstractPlayer p, AbstractMonster m) {
		if (m == null) {
			this.misc = 0;
			this.onMonster = false;
			return;
		}
		this.onMonster = true;
		
		Long max = (long) m.maxHealth;
		Long rate = (long) this.magicNumber;
		Long amount = max * rate / 100L;
		if (amount > 2147483647L)
			amount = 100000000L;
		this.misc = amount.intValue();
		if (this.misc > m.currentHealth)
			this.misc = m.currentHealth;
	}
	
	public void applyPowers() {
		super.applyPowers();
		this.changeDescription(this.getDesc(), false);
	}

	private String getDesc() {
		return exDesc()[0] + ((this.misc > 0 && this.onMonster && this.isHovered()) ? "(" + this.misc + ")" : "")
				+ exDesc()[1] + exDesc()[(this.upgraded ? 3 : 2)] + exDesc()[4];
	}
	
	public void doublesMagicNumber() {
		int tmp = this.magicNumber;
		this.upgradeMagicNumber(this.magicNumber);
		if (this.magicNumber < 0 && tmp > 0)
			this.upgradeMagicNumber(2000000000 - this.magicNumber);
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
	
	public AbstractCard makeCopy() {
		Bloodthirsty tmp = new Bloodthirsty();
		TO_UPDATE.add(tmp);
		return tmp;
	}

	public AbstractCard makeStatEquivalentCopy() {
		Bloodthirsty tmp = (Bloodthirsty)super.makeStatEquivalentCopy();
		tmp.upgradeMagicNumber(this.magicNumber - tmp.magicNumber);
		return tmp;
	}
	
	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(2);
			this.changeDescription(upgradedDesc(), true);
		}
	}

}