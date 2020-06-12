
package cards.colorless;

import cards.AbstractTestCard;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

import actions.AdversityCounterattackAction;

import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

public class AdversityCounterattack extends AbstractTestCard {
    public static final String ID = "AdversityCounterattack";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int BASE_MGC = 1;
    private static final int INT_AMOUNT = 1;

    public AdversityCounterattack() {
        super(ID, NAME, COST, DESCRIPTION, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY);
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new ApplyPowerAction(m, p, new ArtifactPower(m, this.magicNumber), this.magicNumber));
    	this.addToBot(new ApplyPowerAction(p, p, new VulnerablePower(p, this.magicNumber, false), this.magicNumber));
    	this.addToBot(new ApplyPowerAction(p, p, new IntangiblePlayerPower(p, INT_AMOUNT), INT_AMOUNT));
    	this.addToBot(new AdversityCounterattackAction(p, m, AttackEffect.SLASH_HORIZONTAL));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }
}