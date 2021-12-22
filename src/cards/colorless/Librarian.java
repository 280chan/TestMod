package cards.colorless;

import cards.AbstractTestCard;
import powers.LibrarianPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;

public class Librarian extends AbstractTestCard {
	private static final UIStrings UI = INSTANCE.uiString();
	private static final int BASE_MGC = 1;

	public Librarian() {
		super(-1, CardType.SKILL, CardRarity.RARE, CardTarget.NONE);
		this.magicNumber = this.baseMagicNumber = BASE_MGC;
		this.exhaust = true;
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addTmpXCostActionToBot(this, e -> {
			if (this.magicNumber < 1)
				return;
			CardGroup g = cards(e);
			if (g.size() <= this.magicNumber) {
				g.group.forEach(new CardAdder(p, e)::add);
				return;
			}
			AbstractDungeon.gridSelectScreen.open(g, this.magicNumber, UI.TEXT[0] + this.magicNumber + UI.TEXT[1],
					false);
			this.addTmpActionToTop(() -> {
				AbstractDungeon.gridSelectScreen.selectedCards.forEach(new CardAdder(p, e)::add);
				AbstractDungeon.gridSelectScreen.selectedCards.clear();
			});
		});
	}
	
	private CardGroup cards(int e) {
		CardGroup g = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
		g.group = CardLibrary.cards.values().stream().filter(
				c -> c.color == CardColor.COLORLESS && c.rarity != CardRarity.SPECIAL && c.type != CardType.STATUS)
				.collect(this.toArrayList());
		g.shuffle(AbstractDungeon.cardRandomRng);
		g.group = g.group.stream().limit(e).map(AbstractCard::makeCopy).collect(this.toArrayList());
		g.group.stream().map(c -> c.cardID).forEach(UnlockTracker::markCardAsSeen);
		return g;
	}
	
	class CardAdder {
		AbstractPlayer p;
		int e;

		CardAdder(AbstractPlayer p, int amount) {
			this.p = p;
			this.e = amount;
		}

		public void add(AbstractCard c) {
			c.unhover();
			c.modifyCostForCombat(-c.cost);
			if (Librarian.this.handFull()) {
				AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(c));
				this.p.createHandIsFullDialog();
			} else {
				AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(c));
			}
			if (Librarian.this.upgraded)
				Librarian.this.addToBot(new ApplyPowerAction(this.p, this.p, new LibrarianPower(this.p, c, this.e)));
		}
	}
	
	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upDesc();
		}
	}
}