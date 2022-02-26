
package cards.colorless;

import cards.AbstractTestCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.watcher.EnergyDownPower;

import basemod.helpers.BaseModCardTags;

public class FormForm extends AbstractTestCard {
    private static final int COST = 3;
    private static final int BASE_MGC = 1;

    public FormForm() {
        super(COST, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
        this.tags.add(BaseModCardTags.FORM);
    }

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.atb(apply(p, new EnergyDownPower(p, 3, true)));
		this.addTmpActionToBot(() -> {
			ArrayList<AbstractCard> l = CardLibrary.cards.values().stream().filter(this::check).collect(toArrayList());
			Collections.shuffle(l, new Random(AbstractDungeon.cardRandomRng.randomLong()));
			l.forEach(this::play);
		});
	}
    
    private boolean check(AbstractCard c) {
    	if (c instanceof FormForm)
    		return false;
    	if (c.hasTag(BaseModCardTags.FORM))
    		return true;
    	return c.name.endsWith("Form") || c.name.endsWith("形态") || c.cardID.endsWith("Form");
	}
    
    private void play(AbstractCard c) {
    	c = c.makeCopy();
    	if (this.upgraded)
    		c.upgrade();
    	for (int i = 0; i < this.magicNumber; i++) {
        	AbstractCard t = c.makeSameInstanceOf();
        	t.purgeOnUse = true;
            att(new NewQueueCardAction(t, true, true, true));
    	}
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upDesc();
        }
    }
}