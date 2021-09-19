
package cards.colorless;

import cards.AbstractTestCard;
import utils.MiscMethods;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class Illusory extends AbstractTestCard implements MiscMethods {
    public static final String ID = "Illusory";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int BASE_MGC = 1;

    public Illusory() {
        super(ID, NAME, COST, DESCRIPTION, CardType.SKILL, CardRarity.RARE, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
        this.exhaust = this.isEthereal = true;
    }
    
	public void triggerOnGlowCheck() {
		this.glowColor = AbstractDungeon.player.drawPile.group.stream().anyMatch(c -> c.isEthereal)
				? AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy() : AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
	}
	
    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new DrawCardAction(p, this.magicNumber));
		this.addToBot(new GainEnergyAction((int) p.drawPile.group.stream().filter(c -> c.isEthereal).count()));
		this.addTmpActionToBot(() -> {
			p.drawPile.group.stream().filter(c -> !c.isEthereal).forEach(c -> {
				c.isEthereal = true;
				c.name += "(虚无)";
			});
		});
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }
}