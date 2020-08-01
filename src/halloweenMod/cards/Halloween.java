package halloweenMod.cards;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.common.*;
import halloweenMod.mymod.HalloweenMod;

public class Halloween extends AbstractCard {
    public static final String ID = HalloweenMod.MOD_PREFIX + "Halloween";
    public static final String IMG = "green/skill/hide";
    private static CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = cardStrings.NAME;
    private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    private static final String[] E = {" [R] ", " [G] ", " [B] ", " [W] "};
    private static final int COST = 0;
    private static final int BASE_MGC = 1;
    
    public Halloween() {
        this(0);
        this.cardsToPreview = new Halloween(1);
    }
    
    private Halloween(int toPreview) {
    	super(ID, NAME, IMG, IMG, COST, getDescription(), CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.SELF);
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
        this.exhaust = true;
        for (int i = 0; i < toPreview; i++)
        	this.upgrade();
    }
    
    private static String getDescription() {
		return getDescription(BASE_MGC);
	}
	
    private static String getEnergySymble() {
    	if (AbstractDungeon.player != null) {
			switch (AbstractDungeon.player.chosenClass) {
			case WATCHER:
				return E[3];
			case DEFECT:
				return E[2];
			case THE_SILENT:
				return E[1];
			case IRONCLAD:
			default:
				return E[0];
			}
		} else
			return E[0];
    }
    
	private static String getDescription(int magic) {
		String temp = EXTENDED_DESCRIPTION[0];
		if (magic < 4)
			for (int i = 0; i < magic; i++)
				temp += getEnergySymble();
		else
			temp += " !M! " + getEnergySymble();
		if (magic > 1 && EXTENDED_DESCRIPTION.length > 2)
			return temp + EXTENDED_DESCRIPTION[2];
		else
			return temp + EXTENDED_DESCRIPTION[1];
	}

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, this.magicNumber), this.magicNumber));
    	this.addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber));
    	this.addToBot(new GainEnergyAction(this.magicNumber));
    	this.addToBot(new DrawCardAction(p, this.magicNumber));
    }
    
    public AbstractCard makeCopy() {
        return new Halloween();
    }

    public boolean canUpgrade() {
    	return true;
    }
    
    public void upgrade() {
    	this.timesUpgraded += 1;
    	this.upgraded = true;
        this.upgradeMagicNumber(1);
        this.rawDescription = getDescription(this.magicNumber);
        this.name = (NAME + "+" + this.timesUpgraded);
        this.initializeDescription();
        this.initializeTitle();
        if (this.cardsToPreview != null)
        	this.cardsToPreview.upgrade();
    }
}