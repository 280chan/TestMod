
package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.IllusoryAction;

import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class Illusory extends AbstractTestCard {
    public static final String ID = "Illusory";
	private static final CardStrings cardStrings = AbstractTestCard.Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final String[] E = { " [R] ", " [G] ", " [B] ", " [W] " };
    private static final int COST = 1;
    private static final int BASE_ENG = 1;
    private static final int BASE_MGC = 1;

    public Illusory() {
        super(ID, NAME, COST, getDescription(BASE_ENG), CardType.SKILL, CardRarity.RARE, CardTarget.NONE);
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
        this.exhaust = true;
    }

	public static String getDescription(int mgc) {
		String e = E[0];
		if (AbstractDungeon.player != null) {
			switch (AbstractDungeon.player.chosenClass) {
			case WATCHER:
				e = E[3];
				break;
			case DEFECT:
				e = E[2];
				break;
			case THE_SILENT:
				e = E[1];
			default:
			}
		}
		return EXTENDED_DESCRIPTION[0] + e + EXTENDED_DESCRIPTION[1];
	}
    
	public void triggerOnGlowCheck() {
		this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
		for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
			if (c.isEthereal) {
				this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
				break;
			}
		}
	}
	
    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new DrawCardAction(p, this.magicNumber));
    	this.addToBot(new IllusoryAction());
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }
}