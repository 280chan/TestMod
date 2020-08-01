
package halloweenMod.cards;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;
import halloweenMod.mymod.HalloweenMod;
import halloweenMod.powers.GhostCostumePower;
import halloweenMod.utils.HalloweenMiscMethods;

public class GhostCostume extends AbstractCard implements HalloweenMiscMethods {
    public static final String ID = HalloweenMod.MOD_PREFIX + "GhostCostume";
    public static final String IMG = "blue/power/creativeAi";
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = cardStrings.NAME;
    private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    private static final int COST = 1;

    public GhostCostume() {
        super(ID, NAME, IMG, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.SELF);
        
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	if (this.upgraded)
    		this.addRandomPower(p);
    	this.addToBot(new ApplyPowerAction(p, p, new GhostCostumePower(p, 1), 1));
    }
    
    public AbstractCard makeCopy() {
        return new GhostCostume();
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.rawDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}