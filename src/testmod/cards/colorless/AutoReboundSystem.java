
package testmod.cards.colorless;

import testmod.cards.AbstractTestCard;
import testmod.powers.AutoReboundPower;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.BerserkPower;
import com.megacrit.cardcrawl.powers.DrawPower;

public class AutoReboundSystem extends AbstractTestCard {
	
	public String getDescription(int magic) {
		String temp = exDesc()[0];
		String e = " [E]";
		if (magic < 4 && magic > 0) {
			for (int i = 0; i < magic; i++)
				temp += e;
		} else
			temp += magic + e;
		temp += exDesc()[1];
		return temp;
	}
	
	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.atb(apply(p, new AutoReboundPower(p, this.magicNumber)));
		this.atb(apply(p, new BerserkPower(p, this.magicNumber)));
		this.atb(apply(p, new DrawPower(p, this.magicNumber)));
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(1);
			this.rawDescription = getDescription(magicNumber);
			this.initializeDescription();
		}
	}
}