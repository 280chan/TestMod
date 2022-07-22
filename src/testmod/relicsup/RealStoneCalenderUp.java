package testmod.relicsup;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class RealStoneCalenderUp extends AbstractUpgradedRelic {
	
	public RealStoneCalenderUp() {
		super(RelicTier.RARE, LandingSound.HEAVY);
	}
	
	public void atBattleStart() {
		this.counter = 0;
    }
	
	private void dealDamage() {
		int size = (int) AbstractDungeon.getMonsters().monsters.stream().filter(m -> !m.isDeadOrEscaped()).count();
		int temp = this.counter * this.counter * (this.counter < size || size == 0 ? 1 : this.counter / size);
		this.atb(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(temp, true), DamageType.THORNS,
				AttackEffect.BLUNT_HEAVY));
		this.show();
	}
	
	public void atTurnStart() {
		this.counter++;
		this.dealDamage();
	}
	
	public void onPlayerEndTurn() {
		this.dealDamage();
	}
	
	public void onVictory() {
		this.counter = -1;
    }
}