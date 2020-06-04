package relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;

public class Maize extends MyRelic {
	public static final String ID = "Maize";
	private static final int AMOUNT = 5;
	
	public Maize() {
		super(ID, RelicTier.RARE, LandingSound.MAGICAL);
	}

	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void atTurnStart() {
		this.counter = 0;
	}

	public void onUseCard(AbstractCard card, UseCardAction action) {
		if (card.type == AbstractCard.CardType.SKILL) {
			this.counter++;
			if (this.counter == AMOUNT) {
				this.show();
				AbstractPlayer p = AbstractDungeon.player;
				this.addToBot(new ApplyPowerAction(p, p, new IntangiblePlayerPower(p, 1), 1));
			}
		}
	}

	public void onVictory() {
		this.counter = -1;
	}

}