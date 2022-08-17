package testmod.relics;

import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;

public class StomachOfGluttonous extends AbstractTestRelic {
	
	public StomachOfGluttonous() {
		this.counter = 0;
	}
	
	public void onMonsterDeath(final AbstractMonster m) {
		if (m.type == EnemyType.ELITE || m.type == EnemyType.BOSS) {
			this.atb(new HealAction(p(), p(), p().maxHealth));
			this.counter += (p().currentHealth * 100.0 / p().maxHealth);
		} else if (p().maxHealth > 9) {
			this.atb(new HealAction(p(), p(), p().maxHealth / 10));
		}
    }
	
	public float preChangeMaxHP(float amount) {
		return (amount > 0) ? (100 + this.counter) * amount / 100 : amount;
	}

}