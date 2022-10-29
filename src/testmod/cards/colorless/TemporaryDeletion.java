package testmod.cards.colorless;

import testmod.cards.AbstractTestCard;
import testmod.powers.TemporaryDeletionPower;

import java.util.ArrayList;
import java.util.stream.Stream;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.*;

public class TemporaryDeletion extends AbstractTestCard {
	private static final UIStrings UI = MISC.uiString();

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addTmpActionToBot(() -> {
			CardGroup g = new CardGroup(CardGroupType.UNSPECIFIED);
			this.combatCards().forEach(g.group::add);
			g.removeCard(this);
			p.hand.group.forEach(AbstractCard::beginGlowing);
			
			if (g.group.size() == 1) {
				deleteCard(p, g.getTopCard());
				return;
			} else if (g.group.isEmpty()) {
				return;
			}
			AbstractDungeon.gridSelectScreen.open(g, 1, UI.TEXT[0], false, false, false, false);
			this.addTmpActionToTop(() -> {
				deleteCard(p, AbstractDungeon.gridSelectScreen.selectedCards.get(0));
				AbstractDungeon.gridSelectScreen.selectedCards.clear();
			});
		});
	}
	
	private void deleteCard(AbstractPlayer p, AbstractCard c) {
		this.addToTop(apply(p, new TemporaryDeletionPower(p, 1, c)));
		Stream.of(p.drawPile, p.hand, p.discardPile).forEach(g -> deleteCards(g, c.rarity));
	}
	
	private void deleteCards(CardGroup g, CardRarity rarity) {
		ArrayList<AbstractCard> tmp = g.group.stream().filter(c -> c.rarity != rarity).collect(this.toArrayList());;
		g.group.clear();
		g.group = tmp;
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeBaseCost(0);
		}
	}
}