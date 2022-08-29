package testmod.relics;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;

public class ObsoleteBoomerang extends AbstractTestRelic {

	public void onUseCard(final AbstractCard targetCard, final UseCardAction useCardAction) {
		this.atb(this.randomDamage(++this.counter, DamageType.THORNS));
		this.show();
	}
	
	public void onEquip() {
		if (this.inCombat())
			this.counter = 0;
	}
	
	public void atPreBattle() {
		this.counter = 0;
	}

	public void atTurnStart() {
		this.counter = 0;
	}
	
	public void onVictory() {
		this.counter = -1;
	}
	
}