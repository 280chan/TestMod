
package testmod.cards.colorless;

import java.util.stream.Stream;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.*;

import testmod.cards.AbstractTestCard;

import com.megacrit.cardcrawl.localization.UIStrings;

public class Enchant extends AbstractTestCard {
	private static final UIStrings UI = INSTANCE.uiString();
    private static final int COST = 1;
    private static final int BASE_MGC = 2;

    public Enchant() {
        super(COST, CardType.SKILL, CardRarity.RARE, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
        this.exhaust = true;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addTmpActionToBot(() -> {
			CardGroup tmp = new CardGroup(CardGroupType.UNSPECIFIED);
			tmp.group = Stream.of(p.drawPile, p.hand, p.discardPile).flatMap(g -> g.group.stream()).filter(this::filter)
					.collect(toArrayList());
			String msg = UI.TEXT[0] + this.magicNumber + UI.TEXT[1];
			if (tmp.size() > 1) {
				AbstractDungeon.gridSelectScreen.open(tmp, 1, msg, false);
			} else if (tmp.size() == 1) {
				affect(tmp.getTopCard());
				return;
			} else {
				return;
			}
			this.addTmpActionToTop(() -> {
				if (AbstractDungeon.gridSelectScreen.selectedCards.size() != 0) {
					affect(AbstractDungeon.gridSelectScreen.selectedCards.get(0));
					AbstractDungeon.gridSelectScreen.selectedCards.clear();
				}
			});
    	});
    }
    
    private boolean filter(AbstractCard c) {
    	return c != this && !(c instanceof Mystery) && !(c instanceof PerfectCombo) && c.magicNumber != -1;
    }
    
    private void affect(AbstractCard c) {
    	this.addTmpActionToTop(() -> {
    		c.baseMagicNumber *= this.magicNumber;
    		c.magicNumber = c.baseMagicNumber;
    		c.upgradedMagicNumber = true;
    		c.exhaustOnUseOnce = true;
    		c.initializeDescription();
    	});
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.exhaust = false;
            this.upDesc();
        }
    }
}