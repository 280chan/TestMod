package testmod.relicsup;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.powers.BlurPower;

public class CrystalShieldUp extends AbstractUpgradedRelic {
	
	public CrystalShieldUp() {
		super(RelicTier.RARE, LandingSound.CLINK);
	}

	public void atTurnStart() {
		this.counter = -1;
		this.stopPulse();
    }
	
	public void onPlayerEndTurn() {
		if (this.counter == -2) {
			this.att(apply(p(), new BlurPower(p(), 1)));
			this.stopPulse();
		} else {
			this.counter = -2;
		}
    }
	
	public void onVictory() {
		this.counter = -2;
		this.stopPulse();
    }
	
	public int onPlayerGainedBlock(float blockAmount) {
		int retVal = MathUtils.floor(blockAmount);
		if (retVal > 0) {
			this.counter = -2;
			this.beginLongPulse();
		}
		return retVal;
	}
	
	private double damage(double damage) {
		return damage / 2;
	}
	
	private int finalDamage(int damage) {
		return this.relicStream(CrystalShieldUp.class).map(r -> get(r::damage)).reduce(t(), this::chain)
				.apply(damage * 1.0).intValue();
	}
	
	public int onAttackedToChangeDamage(DamageInfo info, int damage) {
		if (this.isActive && info.owner != null && !info.owner.isPlayer && p().hasPower("Blur") && damage > 0) {
			this.att(new DamageAction(info.owner, new DamageInfo(p(), damage - finalDamage(damage), DamageType.THORNS),
					AttackEffect.SLASH_HORIZONTAL, true));
			return finalDamage(damage);
		}
		return damage;
	}
	
}