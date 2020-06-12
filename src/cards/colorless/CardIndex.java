
package cards.colorless;

import cards.AbstractUpdatableCard;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;

import actions.CardIndexAction;

import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;

import java.util.ArrayList;

public class CardIndex extends AbstractUpdatableCard {
    public static final String ID = "CardIndex";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    private static final int COST = 3;
    private static final int BASE_MGC = 2;

    private ArrayList<AbstractCard> cards = new ArrayList<AbstractCard>();
    
    public CardIndex() {
        super(ID, NAME, COST, DESCRIPTION, CardType.SKILL, CardRarity.RARE, CardTarget.ENEMY);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new CardIndexAction(this, m, this.cards));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }

	@Override
	public void preApplyPowers(AbstractPlayer p, AbstractMonster m) {
		if (this.cards.isEmpty() || this.cards.get(0) == null)
			return;
		for (AbstractCard c : this.cards) {
			if (c instanceof AbstractUpdatableCard) {
				((AbstractUpdatableCard)c).preApplyPowers(p, m);
			}
		}
	}
	
	public void applyPowers() {
		super.applyPowers();
		if (this.cards.isEmpty())
			return;
		if (this.cards.get(0) == null) {
			this.changeDescription(EXTENDED_DESCRIPTION[1], true);
			return;
		}
		String tmp = EXTENDED_DESCRIPTION[0];
		for (AbstractCard c : this.cards) {
			c.applyPowers();
			tmp += c.name + EXTENDED_DESCRIPTION[2];
		}
		this.changeDescription(tmp.substring(0, tmp.length() - 1) + EXTENDED_DESCRIPTION[3], true);
	}
	
	public void calculateCardDamage(AbstractMonster m) {
		if (this.cards.isEmpty() || this.cards.get(0) == null)
			return;
    	for (AbstractCard c : this.cards)
    		c.calculateCardDamage(m);
	}
	
	public void tookDamage() {
		if (this.cards.isEmpty() || this.cards.get(0) == null)
			return;
    	for (AbstractCard c : this.cards)
    		c.tookDamage();
	}

	public void didDiscard() {
		if (this.cards.isEmpty() || this.cards.get(0) == null)
			return;
    	for (AbstractCard c : this.cards)
    		c.didDiscard();
	}

	public void switchedStance() {
		if (this.cards.isEmpty() || this.cards.get(0) == null)
			return;
    	for (AbstractCard c : this.cards)
    		c.switchedStance();
	}
	
	public void resetAttributes() {
		super.resetAttributes();
		if (this.cards.isEmpty() || this.cards.get(0) == null)
			return;
    	for (AbstractCard c : this.cards)
    		c.resetAttributes();
	}
	
	public void triggerWhenDrawn() {
		if (this.cards.isEmpty() || this.cards.get(0) == null)
			return;
    	for (AbstractCard c : this.cards)
    		c.triggerWhenDrawn();
	}

	public void triggerWhenCopied() {
		if (this.cards.isEmpty() || this.cards.get(0) == null)
			return;
    	for (AbstractCard c : this.cards)
    		c.triggerWhenCopied();
	}
	
	public AbstractCard makeCopy() {
		CardIndex tmp = new CardIndex();
		TO_UPDATE.add(tmp);
		return tmp;
	}
	
	public AbstractCard makeStatEquivalentCopy() {
		AbstractCard tmp = super.makeStatEquivalentCopy();

		if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null
				|| AbstractDungeon.player.masterDeck.group == null)
			return tmp;

		boolean fromDeck = false;
		for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
			if (c == this)
				fromDeck = true;
		if (!fromDeck)
			((CardIndex) tmp).cards = this.cards;
		
		return tmp;
	}
	
	public void triggerOnOtherCardPlayed(AbstractCard card) {
		if (this.cards.isEmpty() || this.cards.get(0) == null)
			return;
    	for (AbstractCard c : this.cards)
    		c.triggerOnOtherCardPlayed(card);
	}
	
	public void triggerOnCardPlayed(AbstractCard card) {
		if (this.cards.isEmpty() || this.cards.get(0) == null)
			return;
    	for (AbstractCard c : this.cards)
    		c.triggerOnCardPlayed(card);
    }
	
	public void triggerOnScry() {
		if (this.cards.isEmpty() || this.cards.get(0) == null)
			return;
    	for (AbstractCard c : this.cards)
    		c.triggerOnScry();
	}
	
	public void onPlayCard(AbstractCard card, AbstractMonster m) {
		if (this.cards.isEmpty() || this.cards.get(0) == null)
			return;
    	for (AbstractCard c : this.cards)
    		c.onPlayCard(card, m);
	}
	
	public void onRetained() {
		if (this.cards.isEmpty() || this.cards.get(0) == null)
			return;
    	for (AbstractCard c : this.cards)
    		c.onRetained();
	}
	
	public void triggerOnExhaust() {
		if (this.cards.isEmpty() || this.cards.get(0) == null)
			return;
    	for (AbstractCard c : this.cards)
    		c.triggerOnExhaust();
	}
	
	public void clearPowers() {
		super.clearPowers();
		if (this.cards.isEmpty() || this.cards.get(0) == null)
			return;
    	for (AbstractCard c : this.cards)
    		c.clearPowers();
	}
	
	public String toString() {
		if (this.cards.isEmpty() || this.cards.get(0) == null)
			return this.name;
		String tmp = this.name + ":[";
    	for (AbstractCard c : this.cards)
    		tmp += c.name + ",";
    	return tmp.substring(0, tmp.length() - 1) + "]";
	}
	
}