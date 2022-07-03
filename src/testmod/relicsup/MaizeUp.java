package testmod.relicsup;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;

public class MaizeUp extends AbstractUpgradedRelic {
	boolean key = true;
	
	public MaizeUp() {
		super(RelicTier.RARE, LandingSound.MAGICAL);
	}

	public void onEquip() {
		this.counter = 0;
	}
	
	public void atPreBattle() {
		key = true;
	}

	public void onUseCard(AbstractCard card, UseCardAction action) {
		if (card.type == AbstractCard.CardType.SKILL || card.type == AbstractCard.CardType.POWER) {
			if ((++this.counter % 5) == 0) {
				this.show();
				this.stopPulse();
				this.atb(apply(p(), new IntangiblePlayerPower(p(), 1)));
			} else if (this.counter % 5 == 4) {
				this.beginLongPulse();
			}
			if (this.counter % 50 == 0 && key) {
				key = false;
				this.addRandomKey();
			}
		}
	}

}