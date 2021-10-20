package cards.colorless;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.AttackDamageRandomEnemyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster.Intent;
import basemod.ReflectionHacks;
import cards.AbstractTestCard;

public class WeaknessCounterattack extends AbstractTestCard {
    private static final int BASE_DMG = 9;
    private static final int BASE_MGC = 2;

    public WeaknessCounterattack() {
        super(WeaknessCounterattack.class, 1, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ALL_ENEMY);
        this.baseDamage = BASE_DMG;
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
		this.getIdenticalList(AttackEffect.SLASH_HORIZONTAL, this.magicNumber + calculateBonus())
				.forEach(e -> this.addToBot(new AttackDamageRandomEnemyAction(this, e)));
    }
    
    private int calculateBonus() {
    	int count = 0;
		for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
			if (m.isDead || m.escaped)
				continue;
			if (m.getClass().getSuperclass().getCanonicalName().equals("charbosses.bosses.AbstractCharBoss")) {
				try {
					for (AbstractCard c : ((CardGroup)m.getClass().getField("hand").get(m)).group) {
						if (c.type == CardType.ATTACK) {
							if (c.magicNumber <= 1)
								count++;
							else
								count += c.magicNumber;
						}
					}
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
						| SecurityException e) {
					e.printStackTrace();
				}
			} else {
				if (checkIntent(m.intent)) {
					try {
						int amount = ReflectionHacks.getPrivate(m, AbstractMonster.class, "intentMultiAmt");
						if (amount > 1)
							count += amount;
						else
							count++;
					} catch (SecurityException | IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return count;
    }
    
    private static boolean checkIntent(Intent i) {
    	if (i == Intent.ATTACK)
    		return true;
    	if (i == Intent.ATTACK_BUFF)
    		return true;
    	if (i == Intent.ATTACK_DEBUFF)
    		return true;
    	if (i == Intent.ATTACK_DEFEND)
    		return true;
    	return false;
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }
    }
}