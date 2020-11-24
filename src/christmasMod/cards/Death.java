package christmasMod.cards;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.*;

import christmasMod.actions.DeathAction;

public class Death extends AbstractChristmasCard {
    public static final String ID = "Death";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int BASE_ATK = 20;
    private static final int BASE_MGC = 1;
    
    public Death() {
        super(ID, NAME, COST, DESCRIPTION, CardType.ATTACK, CardTarget.ENEMY);
        this.baseDamage = BASE_ATK;
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.addToBot(new DeathAction(m, new DamageInfo(p, this.baseDamage, this.damageTypeForTurn), 1, this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(5);
        }
    }
}