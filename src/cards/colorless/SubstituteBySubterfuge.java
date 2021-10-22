package cards.colorless;

import cards.AbstractTestCard;

import com.evacipated.cardcrawl.mod.stslib.actions.common.MoveCardsAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class SubstituteBySubterfuge extends AbstractTestCard {

	public SubstituteBySubterfuge() {
		super(0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.NONE);
		this.exhaust = true;
	}
	
	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(new GainEnergyAction(1));
		this.addTmpActionToBot(() -> {
			this.addTmpActionToTop(() -> {
				p.relics.forEach(AbstractRelic::onShuffle);
				p.drawPile.shuffle(AbstractDungeon.shuffleRng);
			});
			if (!p.drawPile.isEmpty()) {
				CardGroup g = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
				g.group.addAll(p.drawPile.group);
				AbstractDungeon.gridSelectScreen.open(g, g.size(), true, "选择丢弃任意张牌");
				this.addTmpActionToTop(() -> {
					AbstractDungeon.gridSelectScreen.selectedCards.forEach(p.drawPile::moveToDiscardPile);
					AbstractDungeon.gridSelectScreen.selectedCards.clear();
				});
			}
			this.addToTop(new MoveCardsAction(p.drawPile, p.discardPile, p.discardPile.size()));
		});
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upDesc();
			this.exhaust = false;
		}
	}
}