package cards.colorless;

import cards.AbstractTestCard;
import powers.TemporaryDeletionPower;
import java.util.stream.Stream;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.*;

public class TemporaryDeletion extends AbstractTestCard {

    public TemporaryDeletion() {
        super(TemporaryDeletion.class, 2, CardType.POWER, CardRarity.UNCOMMON, CardTarget.NONE);
        this.isEthereal = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
        this.addTmpActionToBot(() -> {
        	CardGroup g = new CardGroup(CardGroupType.UNSPECIFIED);
        	Stream.of(p.drawPile, p.hand, p.discardPile).map(c -> c.group).forEach(g.group::addAll);
            g.removeCard(this);
            p.hand.group.forEach(AbstractCard::beginGlowing);
            
			if (g.group.size() == 1) {
				deleteCard(p, g.getTopCard());
				return;
			} else if (g.group.isEmpty()) {
				return;
			}
			String info = "[临时删除]";
			AbstractDungeon.gridSelectScreen.open(g, 1, info, false, false, false, false);
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
		g.group = g.group.stream().filter(c -> c.rarity != rarity).collect(this.collectToArrayList());
	}

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(1);
        }
    }
}