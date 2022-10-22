package christmasMod.cards;

import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;

import com.megacrit.cardcrawl.actions.common.*;

public class GiftVoid extends AbstractChristmasCard {
	public static final String ID = "GiftVoid";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final int COST = 0;
	private static final int BASE_MGC = 1;
	
	public GiftVoid() {
		super(ID, NAME, COST, DESCRIPTION, CardType.SKILL, CardTarget.NONE);
		this.magicNumber = this.baseMagicNumber = BASE_MGC;
		this.exhaust = true;
		this.isEthereal = true;
		this.cardsToPreview = new VoidCard();
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(new MakeTempCardInDiscardAction(new VoidCard(), this.magicNumber));
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(1);
		}
	}
}