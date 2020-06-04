
package cards.colorless;

import cards.AbstractTestCard;
import mymod.TestMod;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.unique.LimitBreakAction;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class LimitFlipper extends AbstractTestCard {
    public static final String ID = "LimitFlipper";
	private static final CardStrings cardStrings = AbstractTestCard.Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int BASE_MGC = 1;
    private static boolean active = false;

    public LimitFlipper() {
        super(ID, NAME, COST, DESCRIPTION, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	if (active) {
    	    this.addToBot(new LimitBreakAction());
		} else {
			this.addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber));
		}
    }

	public void triggerOnGlowCheck() {
		this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
		if (active) {
			this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
		}
	}
	
    public void triggerOnCardPlayed(AbstractCard c) {
		active = c.cardID.equals(TestMod.makeID(ID));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();//改名，其实就是多个+
            this.upgradeMagicNumber(1);
        }
    }
}