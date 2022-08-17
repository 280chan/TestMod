package testmod.relics;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class GremlinBalance extends AbstractTestRelic {
	
	public void atTurnStart() {
		int e = 0, c = 0, hp = p().currentHealth;
		for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
			if (m.isDead || m.halfDead || m.escaped)
				continue;
			if (m.currentHealth <= hp)
				e++;
			if (m.currentHealth >= hp)
				c++;
		}
		if (e > 0)
			this.atb(new GainEnergyAction(e));
		if (c > 0)
			this.atb(new DrawCardAction(c));
    }

}