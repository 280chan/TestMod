package testmod.cards.colorless;

import java.util.stream.Stream;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.actions.common.*;
import testmod.cards.AbstractTestCard;

public class BloodShelter extends AbstractTestCard {
	
	private String getDescription(int value) {
		String tmp = exDesc()[0];
		if (value > -1)
			tmp += "(" + value + ")";
		return tmp + exDesc()[1];
	}
	
	public void use(final AbstractPlayer p, final AbstractMonster m) {
		Stream.of(p, m).forEach(c -> this.atb(new GainBlockAction(c, p, this.block)));
		this.addTmpActionToBot(() -> {
			double rate = Stream.of(p, m).mapToDouble(c -> c.currentHealth * 1.0 / c.maxHealth).sum();
			Stream.of(p, m).forEach(c -> f(c, rate));
		});
	}
	
	private static void f(AbstractCreature c, double rate) {
		c.currentHealth = Math.max(1, (int) (rate / 2 * c.maxHealth));
		c.healthBarUpdatedEvent();
	}
	
	public void calculateCardDamage(AbstractMonster m) {
		double rate = p().currentHealth * 1.0 / p().maxHealth + m.currentHealth * 1.0 / m.maxHealth;
		int prePHealth = p().currentHealth;
		int preMHealth = m.currentHealth;
		int postPHealth = (int) (rate / 2 * p().maxHealth);
		if (postPHealth == 0)
			postPHealth = 1;
		int postMHealth = (int) (rate / 2 * m.maxHealth);
		if (postMHealth == 0)
			postMHealth = 1;
		int amt = Math.abs(prePHealth - postPHealth) + Math.abs(preMHealth - postMHealth);
		this.baseBlock = amt;
		super.calculateCardDamage(m);
		this.rawDescription = getDescription(this.baseBlock);
		this.initializeDescription();
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeBaseCost(2);
		}
	}

}