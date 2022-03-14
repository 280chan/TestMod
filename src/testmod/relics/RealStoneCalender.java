package testmod.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;

public class RealStoneCalender extends AbstractTestRelic {
	
	public RealStoneCalender() {
		super(RelicTier.RARE, LandingSound.HEAVY);
	}
	
	public void atBattleStart() {
		this.counter = 0;
    }
	
	public void onPlayerEndTurn() {
		this.counter++;
		int temp = this.counter * this.counter;
		if (this.counter == 7)
			this.counter = 0;
		this.addToTop(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(temp, true), DamageType.THORNS,
				AttackEffect.BLUNT_HEAVY));
		this.show();
	}
	
	public void onVictory() {
		this.counter = -1;
    }
}