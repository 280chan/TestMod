package testmod.relics;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class NegativeEmotionEnhancer extends AbstractTestRelic {

	public void onEquip() {
		this.addEnergy();
    }
	
	public void onUnequip() {
		this.reduceEnergy();
    }
	
	public void atPreBattle() {
		this.counter = 0;
	}
	
	private int turn() {
		return ++this.counter % 3;
	}
	
	public void atTurnStart() {
		switch (turn()) {
		case 1:
			this.atb(apply(p(), new FrailPower(p(), 1, false)));
			break;
		case 2:
			this.atb(apply(p(), new WeakPower(p(), 1, false)));
			break;
		case 0:
			this.atb(apply(p(), new VulnerablePower(p(), 1, false)));
			break;
		}
    }

	public void onVictory() {
		p().increaseMaxHp(1, true);
	}
	
	public void onMonsterDeath(AbstractMonster m) {
		p().heal(1);
	}
	
}