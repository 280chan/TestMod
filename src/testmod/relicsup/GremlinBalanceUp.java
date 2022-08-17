package testmod.relicsup;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class GremlinBalanceUp extends AbstractUpgradedRelic {
	
	public void atTurnStart() {
		int e = 0, c = 0, hp = p().currentHealth, max = p().maxHealth;
		for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
			if (m.isDead || m.halfDead || m.escaped)
				continue;
			if (m.currentHealth <= max)
				e++;
			if (m.maxHealth >= hp)
				c++;
		}
		if (e > 0)
			this.atb(new GainEnergyAction(e));
		if (c > 0)
			this.atb(new DrawCardAction(c));
		if (e + c > 0) {
			int a = e, b = c;
			this.addTmpActionToBot(() -> p().increaseMaxHp(a + b, true));
		}
    }

}