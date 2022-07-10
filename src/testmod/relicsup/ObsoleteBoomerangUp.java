package testmod.relicsup;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;

public class ObsoleteBoomerangUp extends AbstractUpgradedRelic {
	private int turn = 0;
	
	public ObsoleteBoomerangUp() {
		super(RelicTier.RARE, LandingSound.MAGICAL);
	}

	public void onUseCard(final AbstractCard targetCard, final UseCardAction useCardAction) {
		counter++;
		AttackEffect effect;
		if (counter < 6) { // 胡乱分级的伤害特效
			effect = AttackEffect.BLUNT_LIGHT;
		} else if (counter < 10) {
			effect = AttackEffect.SMASH;
		} else {
			effect = AttackEffect.BLUNT_HEAVY;
		}
		this.atb(new DamageAllEnemiesAction(p(), this.counter, DamageType.THORNS, effect));
		this.show();
	}
	
	public void atPreBattle() {
		this.turn = 0;
		this.counter = 0;
	}

	public void atTurnStart() {
		this.turn++;
		this.counter = Math.min(this.turn - 1, this.counter / 4);
	}
	
	public void onVictory() {
		this.turn = 0;
		this.counter = -1;
	}
	
}