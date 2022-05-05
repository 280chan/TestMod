
package testmod.cards.colorless;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.localization.UIStrings;
import testmod.cards.AbstractTestCard;

public class Enchant extends AbstractTestCard {
	private static final UIStrings UI = MISC.uiString();
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
			tmp.group = this.combatCards().filter(this::filter).collect(toArrayList());
			if (tmp.size() > 1) {
				AbstractDungeon.gridSelectScreen.open(tmp, 1, UI.TEXT[0] + this.magicNumber + UI.TEXT[1], false);
				this.addTmpActionToTop(() -> {
					if (AbstractDungeon.gridSelectScreen.selectedCards.size() != 0) {
						affect(AbstractDungeon.gridSelectScreen.selectedCards.get(0));
						AbstractDungeon.gridSelectScreen.selectedCards.clear();
					}
				});
			} else if (tmp.size() == 1) {
				affect(tmp.getTopCard());
				return;
			}
    	});
    }
    
    private boolean filter(AbstractCard c) {
    	return c != this && !(c instanceof Mystery) && !(c instanceof PerfectCombo) && c.magicNumber != -1;
    }
    
    private void affect(AbstractCard c) {
    	this.addTmpActionToTop(() -> {
    		c.magicNumber *= this.magicNumber;
    		c.baseMagicNumber = c.magicNumber;
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