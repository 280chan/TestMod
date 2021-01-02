
package cards.colorless;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.LoseBlockAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import cards.AbstractTestCard;

import com.megacrit.cardcrawl.localization.CardStrings;

public class VirtualReality extends AbstractTestCard {
    public static final String ID = "VirtualReality";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 0;
    private static final int BASE_BLK = 40;

    private static int blockGainLastTurn = 0, blockGainThisTurn = 0;
    
    public static void reset() {
    	blockGainLastTurn = blockGainThisTurn = 0;
    }
    
    public static void gainBlock(int amount) {
    	blockGainThisTurn += amount;
    }
    
    public static void turnStarts() {
    	blockGainLastTurn = blockGainThisTurn;
    	blockGainThisTurn = 0;
    }
    
    public VirtualReality() {
        super(ID, NAME, COST, DESCRIPTION, CardType.SKILL, CardRarity.RARE, CardTarget.SELF);
        this.baseBlock = BASE_BLK;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new GainBlockAction(p, p, this.block));
    	if (blockGainLastTurn > 0)
    		this.addToBot(new LoseBlockAction(p, p, blockGainLastTurn));
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(10);
        }
    }
}