
package cards.mahjong;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.*;

public class MahjongSs extends AbstractMahjongCard {
    public static final String DESCRIPTIONS[] = {"对所有敌人造成 !D! 点伤害。 消耗 。 虚无 。", "造成 !D! 点伤害。 消耗 。 虚无 。", "对随机敌人造成 !D! 点伤害 !M! 次。 消耗 。 虚无 。"};
    private static final int COLOR = 2;
    private int mode;

    public MahjongSs() {
        this(1);
    }
    
    public MahjongSs(int num) {
        super(COLOR, num, setString(num), CardType.ATTACK, setTarget(num));
        this.baseDamage = this.setBaseDamage();
        if (this.mode == 2) {
        	this.baseMagicNumber = 2;
        	this.magicNumber = this.baseMagicNumber;
        } else if (this.mode == 0) {
        	this.isMultiDamage = true;
        }
        this.exhaust = true;
        this.isEthereal = true;
    }
    
    private int setBaseDamage() {
    	int tmp = this.num();
        if (tmp == 0) {
        	tmp = 5;
        	this.mode = 1;
        	return 2 * tmp;
        }
        this.mode = (tmp - 1) % 3;
        return tmp;
    }
    
    private static String setString(int num) {
    	if (num == 0)
    		num = 5;
    	return DESCRIPTIONS[(num - 1) % 3];
    }

    private static CardTarget setTarget(int num) {
    	if (num == 0)
    		num = 5;
    	if ((num - 1) % 3 == 1)
    		return CardTarget.ENEMY;
    	return CardTarget.ALL_ENEMY;
    }
    
	public void use(final AbstractPlayer p, final AbstractMonster m) {
		if (this.mode == 0) {
			this.addToBot(
					new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AttackEffect.SLASH_HEAVY));
		} else if (this.mode == 1) {
			this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
					AttackEffect.SLASH_DIAGONAL));
		} else {
			for (int i = 0; i < this.magicNumber; i++) {
				this.addToBot(new AttackDamageRandomEnemyAction(this, AttackEffect.SLASH_HORIZONTAL));
			}
		}
	}
    
    public AbstractCard makeCopy() {
        return new MahjongSs(this.num());
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            if (this.mode == 2)
            	this.upgradeMagicNumber(1);
            else
            	this.upgradeDamage(1);
        }
    }
}