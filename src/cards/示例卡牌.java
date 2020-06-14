
package cards;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.localization.CardStrings;

/**
 * @deprecated
 */
public class 示例卡牌 extends AbstractTestCard {
    public static final String ID = "Modbasecard";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = -2;
    private static final int BASE_BLK = 5;
    private static final int BASE_DMG = 6;
    private static final int BASE_MGC = 1;

    public 示例卡牌() {
        super(ID, NAME, COST, DESCRIPTION, CardType.ATTACK, CardRarity.RARE, CardTarget.NONE);
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(3);
            this.upgradeDamage(3);
            this.upgradeMagicNumber(1);
            this.upgradeBaseCost(1);
        }
    }
}