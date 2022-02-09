
package cards.colorless;

import cards.AbstractTestCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.EchoForm;
import com.megacrit.cardcrawl.cards.green.WraithForm;
import com.megacrit.cardcrawl.cards.purple.DevaForm;
import com.megacrit.cardcrawl.cards.red.DemonForm;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.watcher.EnergyDownPower;

public class FormForm extends AbstractTestCard {
    private static final int COST = 3;
    private static final int BASE_MGC = 1;

    public FormForm() {
        super(COST, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
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
    	if (c.name.endsWith("Form") || c.name.endsWith("形态") || c.cardID.endsWith("Form"))
    		return true;
		return Stream.of(DemonForm.class, WraithForm.class, EchoForm.class, DevaForm.class)
				.anyMatch(a -> a.isAssignableFrom(c.getClass()));
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