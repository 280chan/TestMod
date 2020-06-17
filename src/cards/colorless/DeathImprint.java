
package cards.colorless;

import powers.DeathImprintPower;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.AbstractPower;

import actions.DeathImprintAction;
import cards.AbstractTestCard;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.CardStrings;

public class DeathImprint extends AbstractTestCard {
    public static final String ID = "DeathImprint";
	private static final CardStrings cardStrings = Strings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 2;
    private static final int BASE_DMG = 8;
    private static final int BASE_MGC = 80;
    public boolean same = false;

    public DeathImprint() {
        super(ID, NAME, COST, DESCRIPTION, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseDamage = BASE_DMG;
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new DeathImprintAction(p, m, this.damage, this.damageTypeForTurn));
    }

	public void triggerOnGlowCheck() {
		this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if ((!m.isDeadOrEscaped()) && (DeathImprintPower.hasThis(m))) {
				this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
				break;
			}
		}
	}
    
	private int fakeCardDamage(AbstractMonster m) {
		AbstractPlayer player = AbstractDungeon.player;
		this.isDamageModified = false;
		float tmp = this.baseDamage;
		if (m != null) {
			if (DeathImprintPower.hasThis(m)) {
				tmp += DeathImprintPower.getThis(m).amount * this.magicNumber / 100f;
				if (this.baseDamage != (int) tmp) {
					this.isDamageModified = true;
				}
			}
		}
		if ((!this.isMultiDamage) && (m != null)) {
			if ((AbstractDungeon.player.hasRelic("WristBlade")) && ((this.costForTurn == 0) || (this.freeToPlayOnce))) {
				tmp += 3.0F;
				if (this.baseDamage != (int) tmp) {
					this.isDamageModified = true;
				}
			}
			for (AbstractPower p : player.powers) {
				tmp = p.atDamageGive(tmp, this.damageTypeForTurn);
				if (this.baseDamage != (int) tmp) {
					this.isDamageModified = true;
				}
			}
			if (m != null) {
				for (AbstractPower p : m.powers) {
					tmp = p.atDamageReceive(tmp, this.damageTypeForTurn);
				}
			}
			for (AbstractPower p : player.powers) {
				tmp = p.atDamageFinalGive(tmp, this.damageTypeForTurn);
				if (this.baseDamage != (int) tmp) {
					this.isDamageModified = true;
				}
			}
			if (m != null) {
				for (AbstractPower p : m.powers) {
					tmp = p.atDamageFinalReceive(tmp, this.damageTypeForTurn);
					if (this.baseDamage != (int) tmp) {
						this.isDamageModified = true;
					}
				}
			}
			if (tmp < 0.0F) {
				tmp = 0.0F;
			}
		}
		return MathUtils.floor(tmp);
	}
	
	public void calculateCardDamage(AbstractMonster m) {
		int dmg = this.fakeCardDamage(m);
		this.isDamageModified = false;
		int tmp = this.baseDamage;
		if (m != null) {
			if (DeathImprintPower.hasThis(m)) {
				this.baseDamage += DeathImprintPower.getThis(m).amount * this.magicNumber / 100f;
				if (this.baseDamage != tmp) {
					this.isDamageModified = true;
				}
			}
		}
		super.calculateCardDamage(m);
		this.baseDamage = tmp;
		if (dmg > this.damage)
			this.damage = dmg;
	}
    
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(20);
        }
    }

}