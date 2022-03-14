package testmod.relics;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;

public class Maize extends AbstractTestRelic {
	
	public Maize() {
		super(RelicTier.RARE, LandingSound.MAGICAL);
	}

	public void atTurnStart() {
		this.counter = 0;
	}

	public void onUseCard(AbstractCard card, UseCardAction action) {
		if (card.type == AbstractCard.CardType.SKILL) {
			if (++this.counter == 5) {
				this.show();
				this.addToBot(apply(p(), new IntangiblePlayerPower(p(), 1)));
			}
		}
	}

	public void onVictory() {
		this.counter = -1;
	}

}