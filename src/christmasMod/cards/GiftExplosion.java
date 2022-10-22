package christmasMod.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.TheBombPower;

import christmasMod.powers.GiftExplosionPower;

public class GiftExplosion extends AbstractChristmasCard {
	public static final String ID = "GiftExplosion";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final int COST = 2;
	private static final int BASE_MGC = 3;
	private static final int BOMB_AMOUNT = 3;
	
	public GiftExplosion() {
		super(ID, NAME, COST, DESCRIPTION, CardType.POWER, CardTarget.SELF);
		this.baseMagicNumber = BASE_MGC;
		this.magicNumber = this.baseMagicNumber;
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(new ApplyPowerAction(p, p, new GiftExplosionPower(p, this.magicNumber)));
		this.addToBot(new ApplyPowerAction(p, p, new TheBombPower(p, BOMB_AMOUNT, 30), BOMB_AMOUNT));
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(-1);
		}
	}
}