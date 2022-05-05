
package testmod.cards.colorless;

import testmod.actions.DeathImprintAction;
import testmod.cards.AbstractTestCard;
import testmod.powers.DeathImprintPower;

import java.util.function.Supplier;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.dungeons.*;

public class DeathImprint extends AbstractTestCard {
    private static final int BASE_DMG = 8;
    private static final int BASE_MGC = 100;
    public boolean same = false;
	@SuppressWarnings("unchecked")
	private static final Supplier<Boolean> G = () -> AbstractDungeon.getMonsters().monsters.stream()
			.anyMatch(MISC.and(MISC.not(AbstractMonster::isDeadOrEscaped), DeathImprintPower::hasThis));

    public DeathImprint() {
        super(1, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseDamage = BASE_DMG;
        this.magicNumber = this.baseMagicNumber = BASE_MGC;
    }

    public void use(final AbstractPlayer p, final AbstractMonster m) {
    	this.addToBot(new DeathImprintAction(p, m, this.damage, this.damageTypeForTurn));
    }
    
	public void triggerOnGlowCheck() {
		this.glowColor = G.get() ? GOLD_BORDER_GLOW_COLOR.cpy() : BLUE_BORDER_GLOW_COLOR.cpy();
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
			for (AbstractRelic r : player.relics) {
				tmp = r.atDamageModify(tmp, this);
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
			tmp = player.stance.atDamageGive(tmp, this.damageTypeForTurn, this);
			if (this.baseDamage != (int) tmp) {
				this.isDamageModified = true;
			}
			for (AbstractPower p : player.powers) {
				tmp = p.atDamageFinalGive(tmp, this.damageTypeForTurn, this);
			}
			if (tmp < 0.0F) {
				tmp = 0.0F;
			}
			if (this.baseDamage != MathUtils.floor(tmp)) {
				this.isDamageModified = true;
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