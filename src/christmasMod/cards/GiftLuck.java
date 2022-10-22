package christmasMod.cards;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;

import christmasMod.actions.GiftLuckAction;

import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.status.Dazed;

public class GiftLuck extends AbstractChristmasCard {
	public static final String ID = "GiftLuck";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final String UPGRADED_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
	private static final int COST = 0;
	private static final int BASE_MGC = 1;
	private static final int DELTA = 2;
	private static final int DRAW = 1;
	
	public GiftLuck() {
		super(ID, NAME, COST, DESCRIPTION, CardType.SKILL, CardTarget.NONE);
		this.magicNumber = this.baseMagicNumber = BASE_MGC;
		this.misc = DELTA;
		this.draw = DRAW;
		this.exhaust = true;
		this.cardsToPreview = new Dazed();
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(new DrawCardAction(p, this.draw));
		this.addToBot(new GiftLuckAction(p, this.magicNumber, this.misc, this.upgraded));
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(1);
			this.rawDescription = UPGRADED_DESCRIPTION;
			this.initializeDescription();
		}
	}
}