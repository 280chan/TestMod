package testmod.cards.colorless;

import java.util.ArrayList;
import java.util.stream.Stream;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.*;

import testmod.cards.AbstractTestCard;

public class MoneyShot extends AbstractTestCard {
    private static final int BASE_DMG = 0;

    public MoneyShot() {
        super(0, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY);
        this.baseDamage = BASE_DMG;
    }
    
    private ArrayList<Integer> create(int gold) {
    	this.misc = this.baseDamage;
    	ArrayList<Integer> tmp = new ArrayList<Integer>();
    	if (gold > 0) {
    		while (gold > 0) {
    			tmp.add(0, gold % 10);
    			gold /= 10;
    		}
    	} else {
    		tmp.add(0);
    	}
		return tmp;
    }
	
	public void use(final AbstractPlayer p, final AbstractMonster a) {
		this.addTmpActionToBot(() -> create(p.gold).forEach(gold -> m(a).filter(this::alive).forEach(m -> {
			this.baseDamage += gold;
			this.calculateCardDamage(m);
			m.damage(new DamageInfo(p, this.damage));
			if ((m.isDying || m.currentHealth <= 0) && !m.halfDead && !m.hasPower("Minion"))
				p.gainGold(this.damage);
			this.baseDamage = this.misc;
		})));
	}
    
	private Stream<AbstractMonster> m(AbstractMonster m) {
		return this.upgraded ? AbstractDungeon.getMonsters().monsters.stream() : Stream.of(m);
	}
	
	private boolean alive(AbstractMonster m) {
		return !(m.isDead || m.escaped || m.halfDead || m.isDying || m.isEscaping);
	}
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.target = CardTarget.ALL_ENEMY;
            this.isMultiDamage = true;
            this.upDesc();
        }
    }
}