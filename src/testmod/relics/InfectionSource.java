package testmod.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import testmod.powers.InfectionPower;
import testmod.powers.InfectionSourcePower;
import testmod.relicsup.InfectionSourceUp;

public class InfectionSource extends AbstractTestRelic {

	public void atBattleStart() {
		if (this.isActive && this.relicStream(InfectionSourceUp.class).count() == 0) {
			if (this.hasEnemies())
				AbstractDungeon.getMonsters().monsters.stream().filter(m -> !m.isDead && !m.isDying)
						.forEach(m -> m.powers.add(new InfectionPower(m)));
			this.addPower(new InfectionSourcePower());
		}
    }
	
	public void atTurnStart() {
		if (this.isActive && this.relicStream(InfectionSourceUp.class).count() == 0)
			for (AbstractMonster m : AbstractDungeon.getMonsters().monsters)
				if (!(m.halfDead || m.isDead || m.isDying || m.escaped || m.isEscaping || InfectionPower.hasThis(m)))
					m.powers.add(new InfectionPower(m));
	}

}