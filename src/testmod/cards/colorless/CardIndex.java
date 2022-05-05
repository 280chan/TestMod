package testmod.cards.colorless;

import testmod.cards.AbstractUpdatableCard;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;

import java.util.ArrayList;
import java.util.function.Consumer;

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
    	this.addTmpActionToBot(() -> {
    		if (this.cards.isEmpty()) {
        		this.addTmpActionToTop(() -> {
					CardGroup g = new CardGroup(CardGroupType.UNSPECIFIED);
					g.group = this.combatCards().collect(this.toArrayList());
					g.removeCard(this);
					p.hand.group.forEach(AbstractCard::beginGlowing);
					if (g.isEmpty() || this.magicNumber < 1) {
						this.removeCard(null);
						return;
					}
					int size = Math.min(this.magicNumber, g.size());
					String info = EXTENDED_DESCRIPTION[4] + size + EXTENDED_DESCRIPTION[5];
					AbstractDungeon.gridSelectScreen.open(g, size, true, info);
					this.addTmpActionToTop(() -> {
						if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
							AbstractDungeon.gridSelectScreen.selectedCards.clear();
						} else {
							this.removeCard(null);
						}
					});
        		});
        	} else if (this.cards.size() != 1 || this.cards.get(0) != null) {
    			this.cards.forEach(this::setXCostEnergy);
    			this.autoplayInOrder(this, this.cards, m);
    		}
    	});
    }
    
    private void removeCard(AbstractCard c) {
    	if (c != null)
    		this.getSource(c).removeCard(c);
		this.cards.add(c);
	}
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }
    
    private boolean active() {
    	return !(this.cards.isEmpty() || this.cards.get(0) == null);
    }

    private void checkActiveActOnCard(Consumer<? super AbstractCard> action) {
		if (this.active())
			this.cards.forEach(action);
    }
    
	@Override
	public void preApplyPowers(AbstractPlayer p, AbstractMonster m) {
		if (this.active())
			this.cards.stream().filter(c -> c instanceof AbstractUpdatableCard)
					.forEach(c -> ((AbstractUpdatableCard) c).preApplyPowers(p, m));
	}
	
	public void applyPowers() {
		super.applyPowers();
		if (this.cards.isEmpty())
			return;
		if (this.cards.get(0) == null) {
			this.changeDescription(EXTENDED_DESCRIPTION[1], true);
			return;
		}
		String tmp = EXTENDED_DESCRIPTION[0] + this.cards.stream().peek(AbstractCard::applyPowers)
				.map(c -> c.name + EXTENDED_DESCRIPTION[2]).reduce("", (a, b) -> a + b);
		this.changeDescription(tmp.substring(0, tmp.length() - 1) + EXTENDED_DESCRIPTION[3], true);
	}
	
	public void calculateCardDamage(AbstractMonster m) {
		this.checkActiveActOnCard(c -> c.calculateCardDamage(m));
	}
	
	public void tookDamage() {
		this.checkActiveActOnCard(AbstractCard::tookDamage);
	}

	public void didDiscard() {
		this.checkActiveActOnCard(AbstractCard::didDiscard);
	}

	public void switchedStance() {
		this.checkActiveActOnCard(AbstractCard::switchedStance);
	}
	
	public void resetAttributes() {
		super.resetAttributes();
		this.checkActiveActOnCard(AbstractCard::resetAttributes);
	}
	
	public void triggerWhenDrawn() {
		this.checkActiveActOnCard(AbstractCard::triggerWhenDrawn);
	}

	public void triggerWhenCopied() {
		this.checkActiveActOnCard(AbstractCard::triggerWhenCopied);
	}
	
	public AbstractCard makeCopy() {
		CardIndex tmp = new CardIndex();
		TO_UPDATE.add(tmp);
		return tmp;
	}
	
	public AbstractCard makeStatEquivalentCopy() {
		AbstractCard tmp = super.makeStatEquivalentCopy();
		if (p() == null || p().masterDeck == null || p().masterDeck.group == null)
			return tmp;
		if (p().masterDeck.group.stream().noneMatch(c -> c == this))
			((CardIndex) tmp).cards = this.cards;
		return tmp;
	}
	
	public void triggerOnOtherCardPlayed(AbstractCard card) {
		this.checkActiveActOnCard(c -> c.triggerOnOtherCardPlayed(card));
	}
	
	public void triggerOnCardPlayed(AbstractCard card) {
		this.checkActiveActOnCard(c -> c.triggerOnCardPlayed(card));
    }
	
	public void triggerOnScry() {
		this.checkActiveActOnCard(AbstractCard::triggerOnScry);
	}
	
	public void onPlayCard(AbstractCard card, AbstractMonster m) {
		this.checkActiveActOnCard(c -> c.onPlayCard(card, m));
	}
	
	public void onRetained() {
		this.checkActiveActOnCard(AbstractCard::onRetained);
	}
	
	public void triggerOnExhaust() {
		this.checkActiveActOnCard(AbstractCard::triggerOnExhaust);
	}
	
	public void clearPowers() {
		super.clearPowers();
		this.checkActiveActOnCard(AbstractCard::clearPowers);
	}
	
	public String toString() {
		if (!this.active())
			return this.name;
		String tmp = this.name + ":[" + this.cards.stream().map(c -> c.name + ",").reduce("", (u, v) -> u + v);
    	return tmp.substring(0, tmp.length() - 1) + "]";
	}
	
}