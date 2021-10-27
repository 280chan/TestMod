package relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;

public class TwinklingStar extends AbstractTestRelic {
	
	public TwinklingStar() {
		super(RelicTier.COMMON, LandingSound.MAGICAL);
		this.counter = 0;
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0] + f() + DESCRIPTIONS[1];
	}
	
	private int f() {
		return this.getIdenticalList(2, this.counter / 100).stream().reduce(1, (a, b) -> a * b);
	}
	
	public void act() {
		if (this.hasEnemies()) {
			this.counter++;
			if (this.counter % 100 == 99) {
				this.beginLongPulse();
			} else if (this.counter % 100 == 0) {
				this.stopPulse();
				this.updateDescription(p().chosenClass);
			}
			this.flash();
			this.atb(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(f(), true), DamageType.THORNS,
					AttackEffect.LIGHTNING));
		}
	}
	
}