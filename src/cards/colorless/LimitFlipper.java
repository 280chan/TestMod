
package cards.colorless;

import cards.AbstractTestCard;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.unique.LimitBreakAction;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class LimitFlipper extends AbstractTestCard {
    public static final String ID = "LimitFlipper";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    private static final int COST = 1;
    private static final int BASE_MGC = 2;
    private boolean active = false;

    public LimitFlipper() {
        super(ID, NAME, COST, DESCRIPTION, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
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
		if (active)
			this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
	}
	
    public void triggerOnCardPlayed(AbstractCard c) {
    	this.updateActive(c);
    }
    
    private AbstractCard lastCard() {
    	ArrayList<AbstractCard> list = null;
    	if (AbstractDungeon.actionManager != null)
    		list = AbstractDungeon.actionManager.cardsPlayedThisCombat;
    	if (list != null && list.size() > 0)
    		return list.get(0);
    	return null;
    }
    
    private void updateActive(AbstractCard c) {
    	if (c == null)
    		return;
		this.active = this.upgraded ? c.color == CardColor.COLORLESS : c instanceof LimitFlipper;
    }
    
    public AbstractCard makeStatEquivalentCopy() {
    	LimitFlipper tmp = (LimitFlipper) super.makeStatEquivalentCopy();
    	tmp.triggerOnCardPlayed(lastCard());
    	return tmp;
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
            this.rawDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}