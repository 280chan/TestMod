package cards.colorless;

import cards.AbstractTestCard;
import com.megacrit.cardcrawl.actions.unique.LimitBreakAction;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class LimitFlipper extends AbstractTestCard {
    private static final int BASE_MGC = 2;
    private boolean active = false;
    private boolean delayActive = false;

    public LimitFlipper() {
        super(1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		addTmpActionToBot(() -> {
			addTmpActionToTop(() -> this.active |= this.delayActive);
			addToTop(active ? new LimitBreakAction() : apply(p, new StrengthPower(p, this.magicNumber)));
		});
	}

	public void triggerOnGlowCheck() {
		this.glowColor = active ? AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy() : AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
	}
	
    public void triggerOnCardPlayed(AbstractCard c) {
    	if (this.equals(c)) {
    		delayActive = true;
    	} else {
        	this.updateActive(c);
    	}
    }
    
    private AbstractCard lastCard() {
		return AbstractDungeon.actionManager == null || AbstractDungeon.actionManager.cardsPlayedThisCombat == null
				? null : last(AbstractDungeon.actionManager.cardsPlayedThisCombat);
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
            this.upDesc();
        }
    }
}