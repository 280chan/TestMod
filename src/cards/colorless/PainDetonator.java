
package cards.colorless;

import basemod.abstracts.*;
import mymod.TestMod;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

public class PainDetonator extends CustomCard {
    public static final String ID = "PainDetonator";
    public static final String NAME = "痛楚起爆器";
	public static final String IMG = TestMod.cardIMGPath("relic1");
    public static final String DESCRIPTION = "对敌人造成其已失去生命的 !M! %的伤害( !D! )。";
    private static final int COST = 1;
    private static final int BASE_MGC = 30;
    private static final int BASE_DMG = 0;
    
    public PainDetonator() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseDamage = BASE_DMG;
        this.baseMagicNumber = BASE_MGC;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AttackEffect.SLASH_HEAVY));
    }
    
    public void calculateCardDamage(AbstractMonster m) {
    	this.baseDamage = (int) ((m.maxHealth - m.currentHealth) * this.magicNumber / 100f);
    	super.calculateCardDamage(m);
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(20);
        }
    }
}