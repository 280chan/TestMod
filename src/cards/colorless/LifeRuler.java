
package cards.colorless;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.ConstrictedPower;

import basemod.abstracts.CustomCard;
import mymod.TestMod;

import com.megacrit.cardcrawl.dungeons.*;

import com.megacrit.cardcrawl.actions.common.*;

public class LifeRuler extends CustomCard {
	public static final String ID = "LifeRuler";
	public static final String NAME = "生命标尺";
	public static final String IMG = TestMod.cardIMGPath("relic1");
	public static final String DESCRIPTION = "对所有敌人施加你与目标敌人最大生命的最大公约数与当前生命的最大公约数中的较大值";
	private static final int COST = 1;
	private static final String DESCRIPTIONS[] = { "层缠绕。", " 消耗 。" };
	
	public LifeRuler() {
        super(TestMod.makeID(ID), NAME, IMG, COST, DESCRIPTION + DESCRIPTIONS[0] + DESCRIPTIONS[1], CardType.SKILL, CardColor.COLORLESS, CardRarity.RARE, CardTarget.ENEMY);
        this.exhaust = true;
    }

	public void use(final AbstractPlayer p, final AbstractMonster t) {
    	for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!m.isDead && !m.isDying && !m.halfDead) {
				AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new ConstrictedPower(m, p, this.misc), this.misc));
			}
		}
    }

	@Override
	public void calculateCardDamage(AbstractMonster m) {
		AbstractPlayer p = AbstractDungeon.player;
    	if (m == null)
    		this.misc = 0;
    	else
    		this.misc = Math.max(gcd(p.maxHealth, m.maxHealth), gcd(p.currentHealth, m.currentHealth));
    	this.rawDescription = this.getDesc();
		this.initializeDescription();
	}
	
	private static int gcd(int a, int b) {
		return (a == 0) ? b : gcd(b % a, a);
    }
    
    private String getDesc() {
		String tmp = DESCRIPTION;
    	if (this.misc > 0) {
    		tmp += "(" + this.misc + ")";
    	}
    	tmp += DESCRIPTIONS[0];
    	if (!this.upgraded) {
    		tmp += DESCRIPTIONS[1];
    	}
    	return tmp;
	}
    
    public void resetAttributes() {
    	this.rawDescription = DESCRIPTION + DESCRIPTIONS[0];
    	if (!this.upgraded) {
    		this.rawDescription += DESCRIPTIONS[1];
    	}
		this.initializeDescription();
		super.resetAttributes();
    }
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.exhaust = false;
            this.rawDescription = DESCRIPTION + DESCRIPTIONS[0];
    		this.initializeDescription();
        }
    }

}