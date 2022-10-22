package testmod.cards.colorless;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.ConstrictedPower;

import testmod.cards.AbstractTestCard;

import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;

public class LifeRuler extends AbstractTestCard {
	
	public LifeRuler() {
		super(1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ENEMY);
		this.exhaust = true;
	}

	public void use(final AbstractPlayer p, final AbstractMonster t) {
		AbstractDungeon.getMonsters().monsters.stream().filter(not(m -> m.isDead || m.isDying || m.halfDead))
				.map(split(t(), m -> new ConstrictedPower(m, p, this.misc)))
				.forEach(consumer((m, cp) -> this.addToBot(new ApplyPowerAction(m, p, cp, this.misc))));
	}

	@Override
	public void calculateCardDamage(AbstractMonster m) {
		super.calculateCardDamage(m);
		AbstractPlayer p = AbstractDungeon.player;
		this.misc = m == null ? 0 :Math.max(gcd(p.maxHealth, m.maxHealth), gcd(p.currentHealth, m.currentHealth));
		this.rawDescription = this.getDesc();
		this.initializeDescription();
	}
	
	private static int gcd(int a, int b) {
		return (a == 0) ? b : gcd(b % a, a);
	}
	
	private String getDesc() {
		String tmp = this.exDesc()[0];
		if (this.misc > 0) {
			tmp += "(" + this.misc + ")";
		}
		tmp += this.exDesc()[1];
		if (!this.upgraded) {
			tmp += this.exDesc()[2];
		}
		return tmp;
	}
	
	public void resetAttributes() {
		if (!this.upgraded)
			this.upDesc(this.exDesc()[0] + this.exDesc()[1] + this.exDesc()[2]);
		else
			this.upDesc(this.exDesc()[0] + this.exDesc()[1]);
		super.resetAttributes();
	}
	
	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.exhaust = false;
			this.upDesc(this.exDesc()[0] + this.exDesc()[1]);
		}
	}

}