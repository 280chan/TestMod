package testmod.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageRandomEnemyAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;

public class ObsoleteBoomerang extends AbstractTestRelic {

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
		this.atb(new DamageRandomEnemyAction(new DamageInfo(p(), counter, DamageType.THORNS), effect));
		this.show();
	}
	
	public void atPreBattle() {
		this.counter = 0;
	}

	public void atTurnStart() {
		this.counter = 0;
	}
	
	public void onVictory() {
		this.counter = 0;
	}
	
}