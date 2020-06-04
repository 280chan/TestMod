package cards.colorless;

import cards.AbstractEquivalentableCard;
import cards.AbstractTestCard;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.actions.unique.SwordBoomerangAction;

public class Mystery extends AbstractEquivalentableCard {
    public static final String ID = "Mystery";
	private static final CardStrings cardStrings = AbstractTestCard.Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 2;
    private static final int ATTACK_DMG = 4;
    private static final int BASE_MGC = 1;

    public Mystery() {
        super(ID, NAME, COST, DESCRIPTION, CardType.ATTACK, CardRarity.RARE, CardTarget.ALL_ENEMY);
        this.baseDamage = ATTACK_DMG;
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
        this.exhaust = true;
    }

    private static int countMystery() {
    	int count = 0;
    	for (String symbol : CardCrawlGame.metricData.path_taken)
    		if (symbol.equals("?"))
    			count++;
    	return count;
    }
    
    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	System.out.println("当前卡M = " + this.magicNumber);
    	this.applyCount();
    	this.addToBot(new SwordBoomerangAction(
    		      AbstractDungeon.getMonsters().getRandomMonster(true), new DamageInfo(p, this.baseDamage), this.magicNumber));
    }
    
    private void applyCount() {
    	if (this.magicNumber != countMystery() + BASE_MGC)
    		this.upgradeMagicNumber(BASE_MGC - this.magicNumber + countMystery());
    }
    
    public void calculateCardDamage(AbstractMonster m) {
    	this.applyCount();
    	super.calculateCardDamage(m);
    }
    
    public void applyPowers() {
    	this.applyCount();
    	super.applyPowers();
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(2);
        }
    }
}