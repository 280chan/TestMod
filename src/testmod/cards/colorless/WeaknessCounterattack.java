package testmod.cards.colorless;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.AttackDamageRandomEnemyAction;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.*;
import testmod.cards.AbstractTestCard;

public class WeaknessCounterattack extends AbstractTestCard {
    private static final int BASE_DMG = 9;
    private static final int BASE_MGC = 2;

    public WeaknessCounterattack() {
        super(1, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ALL_ENEMY);
        this.baseDamage = BASE_DMG;
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.getIdenticalList(AttackEffect.SLASH_HORIZONTAL, this.magicNumber + calculateBonus())
				.forEach(e -> this.addToBot(new AttackDamageRandomEnemyAction(this, e)));
    }
    
	private int calculateBonus() {
		return AbstractDungeon.getMonsters().monsters.stream().filter(m -> !m.isDead && !m.escaped)
				.mapToInt(this::count).sum();
	}
    
    private int count(AbstractMonster m) {
    	if (m.getClass().getSuperclass().getCanonicalName().equals("charbosses.bosses.AbstractCharBoss")) {
			try {
				return ((CardGroup) m.getClass().getField("hand").get(m)).group.stream()
						.filter(c -> c.type == CardType.ATTACK).mapToInt(c -> Math.max(c.magicNumber, 1)).sum();
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
					| SecurityException e) {
				e.printStackTrace();
				return 0;
			}
		} else {
			return this.damageTimes(m);
		}
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }
}