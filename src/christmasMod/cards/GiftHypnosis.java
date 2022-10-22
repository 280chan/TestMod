package christmasMod.cards;

import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;

import testmod.utils.MiscMethods;

public class GiftHypnosis extends AbstractChristmasCard implements MiscMethods {
	public static final String ID = "GiftHypnosis";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final int COST = 2;
	private static final int BASE_ATK = 20;
	private static final int BASE_MGC = 1;
	
	public GiftHypnosis() {
		super(ID, NAME, COST, DESCRIPTION, CardType.ATTACK, CardTarget.ENEMY);
		this.baseDamage = BASE_ATK;
		this.magicNumber = this.baseMagicNumber = BASE_MGC;
		this.cardsToPreview = new VoidCard();
	}

	public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn)));
		this.addToBot(new MakeTempCardInDiscardAction(new VoidCard(), this.magicNumber));
		this.skipMonsterIntent();
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeDamage(5);
		}
	}
}