package testmod.relics;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;

public class Maize extends AbstractTestRelic {

	public void atTurnStart() {
		this.counter = 0;
		this.stopPulse();
	}

	public void onUseCard(AbstractCard card, UseCardAction action) {
		if (card.type == AbstractCard.CardType.SKILL) {
			if (++this.counter == 5) {
				this.counter = 0;
				this.show();
				this.stopPulse();
				this.atb(apply(p(), new IntangiblePlayerPower(p(), 1)));
			} else if (this.counter == 4) {
				this.beginLongPulse();
			}
		}
	}

	public void onVictory() {
		this.counter = -1;
		this.stopPulse();
	}

}