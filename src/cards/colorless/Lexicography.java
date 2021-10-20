package cards.colorless;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import cards.AbstractTestCard;

public class Lexicography extends AbstractTestCard {
    private static final int BASE_DMG = 0;
    private static final AttackEffect EFFECT = AttackEffect.SLASH_DIAGONAL;

    public Lexicography() {
        super(Lexicography.class, 2, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY);
        this.baseDamage = BASE_DMG;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
		if (this.upgraded) {
			this.addToBot(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, EFFECT));
		} else {
			this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), EFFECT));
		}
    }
    
	private int calculate(AbstractMonster m) {
		return m == null ? BASE_DMG
				: (int) m.powers.stream().filter(p -> !(p instanceof InvisiblePower))
						.mapToInt(p -> p.description.length()).filter(a -> a != 0).average().orElse(BASE_DMG);
	}
    
    public void calculateCardDamage(AbstractMonster m) {
		this.baseDamage = calculate(m);
    	super.calculateCardDamage(m);
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.isMultiDamage = true;
            this.rawDescription = this.upgradedDesc();
            this.initializeDescription();
        }
    }
}