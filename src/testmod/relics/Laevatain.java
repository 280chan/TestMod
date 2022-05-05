package testmod.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.Omamori;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class Laevatain extends AbstractTestRelic {
	private static final UIStrings UI = MISC.uiString();
	
	public Laevatain() {
		super(RelicTier.RARE, LandingSound.HEAVY);
	}
	
	public void onEquip() {
		for (AbstractCard c : Sins.SINS) {
			AbstractDungeon.curseCardPool.removeCard(c.cardID);
		}
		CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
		for (int i = 0; i < 3; i++) {
			if ((p().hasRelic("Omamori")) && (p().getRelic("Omamori").counter != 0)) {
				((Omamori) p().getRelic("Omamori")).use();
			} else {
				AbstractCard curse = AbstractDungeon.getCard(AbstractCard.CardRarity.CURSE);
				UnlockTracker.markCardAsSeen(curse.cardID);
				group.addToBottom(curse.makeCopy());
			}
		}
	    AbstractDungeon.gridSelectScreen.openConfirmationGrid(group, UI.TEXT[0]);
    }
	
	public void atPreBattle() {
		this.counter = 0;
		this.show();
		this.addToBot(apply(p(), new StrengthPower(p(), 3)));
	}
	
	public void atTurnStart() {
		this.counter++;
		if (this.counter == 3) {
			this.counter = 0;
			if (p().masterDeck.group.stream().anyMatch(c -> c.type == CardType.CURSE)) {
				this.addToBot(apply(p(), new StrengthPower(p(),
						(int) (p().masterDeck.group.stream().filter(c -> c.type == CardType.CURSE).count()))));
				this.show();
			}
		}
    }
	
	public void onVictory() {
		this.counter = -1;
    }
	
}