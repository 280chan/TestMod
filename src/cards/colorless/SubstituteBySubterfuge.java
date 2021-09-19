
package cards.colorless;

import cards.AbstractTestCard;

import com.evacipated.cardcrawl.mod.stslib.actions.common.MoveCardsAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.localization.CardStrings;

public class SubstituteBySubterfuge extends AbstractTestCard {
	public static final String ID = "SubstituteBySubterfuge";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final String E = " [E] ";
	private static final int COST = 0;

	public SubstituteBySubterfuge() {
		super(ID, NAME, COST, getDescription(false), CardType.SKILL, CardRarity.UNCOMMON, CardTarget.NONE);
		this.exhaust = true;
	}

	public static String getDescription(boolean upgraded) {
		String temp = EXTENDED_DESCRIPTION[0] + E + EXTENDED_DESCRIPTION[1];
		if (!upgraded)
			temp += EXTENDED_DESCRIPTION[2];
		return temp;
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
			this.rawDescription = getDescription(true);
			this.initializeDescription();
			this.exhaust = false;
		}
	}
}