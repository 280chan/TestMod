package testmod.cards.colorless;

import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import java.util.ArrayList;
import java.util.function.Consumer;
import testmod.cards.AbstractTestCard;

public class CardIndex extends AbstractTestCard {
	private ArrayList<AbstractCard> cards = new ArrayList<AbstractCard>();

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addTmpActionToBot(() -> {
			if (this.cards.isEmpty()) {
				this.addTmpActionToTop(() -> {
					CardGroup g = new CardGroup(CardGroupType.UNSPECIFIED);
					g.group = this.combatCards().collect(this.toArrayList());
					g.removeCard(this);
					p.hand.group.forEach(AbstractCard::beginGlowing);
					if (g.isEmpty() || this.magicNumber < 1) {
						this.removeCard(null);
						return;
					}
					int size = Math.min(this.magicNumber, g.size());
					String info = exDesc()[4] + size + exDesc()[5];
					AbstractDungeon.gridSelectScreen.open(g, size, true, info);
					this.addTmpActionToTop(() -> {
						if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
							AbstractDungeon.gridSelectScreen.selectedCards.forEach(this::removeCard);
							AbstractCard[] arr = new AbstractCard[this.cards.size()];
							for (int i = 0; i < arr.length; i++)
								arr[i] = this.cards.get(i);
							MultiCardPreview.add(this, arr);
							AbstractDungeon.gridSelectScreen.selectedCards.clear();
						} else {
							this.removeCard(null);
						}
					});
				});
			} else if (this.cards.size() != 1 || this.cards.get(0) != null) {
				this.cards.forEach(this::setXCostEnergy);
				this.autoplayInOrder(this, this.cards, m);
			}
		});
	}
	
	private void removeCard(AbstractCard c) {
		if (c != null)
			this.getSource(c).removeCard(c);
		this.cards.add(c);
	}
	
	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(1);
		}
	}
	
	private boolean active() {
		return !(this.cards.isEmpty() || this.cards.get(0) == null);
	}

	private void checkActiveActOnCard(Consumer<? super AbstractCard> action) {
		if (this.active())
			this.cards.forEach(action);
	}
	
	public void applyPowers() {
		super.applyPowers();
		if (this.cards.isEmpty())
			return;
		if (this.cards.get(0) == null) {
			this.rawDescription = exDesc()[1];
			this.initializeDescription();
			return;
		}
		String tmp = exDesc()[0] + this.cards.stream().peek(AbstractCard::applyPowers).map(c -> c.name + exDesc()[2])
				.reduce("", (a, b) -> a + b);
		this.rawDescription = tmp.substring(0, tmp.length() - 1) + exDesc()[3];
		this.initializeDescription();
	}
	
	public void calculateCardDamage(AbstractMonster m) {
		this.checkActiveActOnCard(c -> c.calculateCardDamage(m));
	}
	
	public void tookDamage() {
		this.checkActiveActOnCard(AbstractCard::tookDamage);
	}

	public void didDiscard() {
		this.checkActiveActOnCard(AbstractCard::didDiscard);
	}

	public void switchedStance() {
		this.checkActiveActOnCard(AbstractCard::switchedStance);
	}
	
	public void resetAttributes() {
		super.resetAttributes();
		this.checkActiveActOnCard(AbstractCard::resetAttributes);
	}
	
	public void triggerWhenDrawn() {
		this.checkActiveActOnCard(AbstractCard::triggerWhenDrawn);
	}

	public void triggerWhenCopied() {
		this.checkActiveActOnCard(AbstractCard::triggerWhenCopied);
	}
	
	public AbstractCard makeStatEquivalentCopy() {
		AbstractCard tmp = super.makeStatEquivalentCopy();
		if (p() == null || p().masterDeck == null || p().masterDeck.group == null)
			return tmp;
		if (p().masterDeck.group.stream().noneMatch(c -> c == this))
			((CardIndex) tmp).cards = this.cards;
		return tmp;
	}
	
	public void triggerOnOtherCardPlayed(AbstractCard card) {
		this.checkActiveActOnCard(c -> c.triggerOnOtherCardPlayed(card));
	}
	
	public void triggerOnCardPlayed(AbstractCard card) {
		this.checkActiveActOnCard(c -> c.triggerOnCardPlayed(card));
	}
	
	public void triggerOnScry() {
		this.checkActiveActOnCard(AbstractCard::triggerOnScry);
	}
	
	public void onPlayCard(AbstractCard card, AbstractMonster m) {
		this.checkActiveActOnCard(c -> c.onPlayCard(card, m));
	}
	
	public void onRetained() {
		this.checkActiveActOnCard(AbstractCard::onRetained);
	}
	
	public void triggerOnExhaust() {
		this.checkActiveActOnCard(AbstractCard::triggerOnExhaust);
	}
	
	public void clearPowers() {
		super.clearPowers();
		this.checkActiveActOnCard(AbstractCard::clearPowers);
	}
	
	public String toString() {
		if (!this.active())
			return this.name;
		String tmp = this.name + ":[" + this.cards.stream().map(c -> c.name + ",").reduce("", (u, v) -> u + v);
		return tmp.substring(0, tmp.length() - 1) + "]";
	}
	
}