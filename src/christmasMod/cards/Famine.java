package christmasMod.cards;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;

import christmasMod.actions.FamineAction;

public class Famine extends AbstractChristmasCard {
	public static final String ID = "Famine";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final int COST = 1;
	private static final int BASE_ATK = 13;
	private static final int BASE_MGC = 5;
	private static final int RATIO = 2;
	
	public Famine() {
		super(ID, NAME, COST, DESCRIPTION, CardType.ATTACK, CardTarget.ENEMY);
		this.baseDamage = BASE_ATK;
		this.baseMagicNumber = BASE_MGC;
		this.magicNumber = this.baseMagicNumber;
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		addToBot(new FamineAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), this.magicNumber, RATIO));
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(2);
		}
	}
}