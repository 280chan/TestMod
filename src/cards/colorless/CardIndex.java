
package cards.colorless;

import cards.AbstractUpdatableCard;
import mymod.TestMod;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.*;

import actions.CardIndexAction;

import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;

import java.util.ArrayList;

public class CardIndex extends AbstractUpdatableCard {
    public static final String ID = "CardIndex";
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(TestMod.makeID(ID));
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    public static final String IMG = TestMod.cardIMGPath("relic1");
    private static final int COST = 3;//卡牌费用
    private static final int BASE_MGC = 2;

    private ArrayList<AbstractCard> cards = new ArrayList<AbstractCard>();
    
    public CardIndex() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.RARE, CardTarget.ENEMY);
        this.baseMagicNumber = BASE_MGC;//特殊值，一般用来叠BUFF层数。和下一行连用。
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	AbstractDungeon.actionManager.addToBottom(new CardIndexAction(this, m, this.cards));
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
	
}