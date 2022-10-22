package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower.PowerType;

public class NegativeEmotionEnhancerUp extends AbstractUpgradedRelic {

	public void onEquip() {
		this.addEnergy();
	}
	
	public void onUnequip() {
		this.reduceEnergy();
	}
	
	private int countDebuff(AbstractCreature c) {
		return (int) c.powers.stream().filter(p -> !(p instanceof InvisiblePower) && p.type == PowerType.DEBUFF).count();
	}
	
	public void atTurnStart() {
		int e = Math.min(countDebuff(p()), p().energy.energyMaster);
		if (e > 0) {
			this.atb(new GainEnergyAction(e));
			this.atb(new DrawCardAction(e));
		}
	}

	public void onVictory() {
		p().increaseMaxHp(Math.max(1, countDebuff(p())), true);
	}
	
	public void onMonsterDeath(AbstractMonster m) {
		p().heal(Math.max(1, countDebuff(m)));
	}
	
}