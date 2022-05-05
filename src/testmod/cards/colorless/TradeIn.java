package testmod.cards.colorless;

import java.util.ArrayList;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.*;

import testmod.cards.AbstractTestCard;

public class TradeIn extends AbstractTestCard {
	private static final UIStrings UI = MISC.uiString();
    private static final int BASE_MGC = 1;

    public TradeIn() {
        super(1, CardType.SKILL, CardRarity.RARE, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
        this.exhaust = this.isEthereal = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addTmpActionToBot(() -> {
			if (!p.hand.isEmpty()) {
				AbstractDungeon.handCardSelectScreen.open(UI.TEXT[0], p.hand.size(), true, true, false, false);
				this.addTmpActionToTop(() -> {
					if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
						int size = AbstractDungeon.handCardSelectScreen.selectedCards.size();
						exhaustsCards(p, AbstractDungeon.handCardSelectScreen.selectedCards.group);
						AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
						AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
						if (size > 0 && !p.drawPile.isEmpty()) {
							this.addTmpActionToTop(() -> {
								CardGroup tmp = new CardGroup(CardGroupType.UNSPECIFIED);
								tmp.group = p.drawPile.group.stream().collect(this.toArrayList());
								if (tmp.size() <= size) {
									tmp.group.stream().forEach(this::moveCard);
									return;
								}
								AbstractDungeon.gridSelectScreen.open(tmp, size,
										UI.TEXT[1] + size + UI.TEXT[2] + this.magicNumber + UI.TEXT[3], false);
								this.addTmpActionToTop(() -> {
									if (AbstractDungeon.gridSelectScreen.selectedCards.size() != 0) {
										AbstractDungeon.gridSelectScreen.selectedCards.stream().forEach(this::moveCard);
										p.hand.refreshHandLayout();
										AbstractDungeon.gridSelectScreen.selectedCards.clear();
									}
								});
							});
						}
					}
				});
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void moveCard(AbstractCard card) {
		combine(AbstractCard::unhover, this::setCard, ifElse(c -> handFull(),
				combine(p().drawPile::moveToDiscardPile, c -> p().createHandIsFullDialog()), p().drawPile::moveToHand))
						.accept(card);
	}

	private void setCard(AbstractCard c) {
		c.exhaustOnUseOnce = true;
		c.setCostForTurn(c.costForTurn - this.magicNumber);
	}

	@SuppressWarnings("unchecked")
	private void exhaustsCards(AbstractPlayer p, ArrayList<AbstractCard> list) {
		list.forEach(combine(p.hand::moveToExhaustPile, c -> c.exhaustOnUseOnce = c.freeToPlayOnce = false));
		CardCrawlGame.dungeon.checkForPactAchievement();
		p.hand.refreshHandLayout();
	}

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }
}