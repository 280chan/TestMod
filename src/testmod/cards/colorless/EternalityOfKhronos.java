package testmod.cards.colorless;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import testmod.cards.AbstractTestCard;

public class EternalityOfKhronos extends AbstractTestCard {

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.skipMonsterIntent();
	}
	
	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.exhaust = false;
			this.upDesc();
		}
	}
}