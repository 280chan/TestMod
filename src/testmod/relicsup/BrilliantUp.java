package testmod.relicsup;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BrilliantUp extends AbstractUpgradedRelic {
	
	private static int damageFunction(int gold) {
		return (int) (Math.cbrt(gold) * Math.cbrt(gold) / 2);
	}
	
	private void applyDamage() {
		int[] dmg = DamageInfo.createDamageMatrix(damageFunction(p().gold), true);
		this.atb(new DamageAllEnemiesAction(null, dmg, DamageType.THORNS, AttackEffect.BLUNT_LIGHT));
	}
	
	private int updateCounter(int input) {
		if (this.counter < 0)
			this.counter = 0;
		this.counter += input;
		return input;
	}
	
	private int getCounter() {
		int tmp = this.counter;
		this.counter = -1;
		return tmp;
	}
	
	private void gainTempGold() {
		int tmp = Math.max(100, p().gold / 10);
		if (this.isActive)
			p().gainGold(this.relicStream(BrilliantUp.class).mapToInt(r -> r.updateCounter(tmp)).sum());
	}
	
	public void atPreBattle() {
		this.gainTempGold();
	}
	
	public void onVictory() {
		if (this.isActive)
			p().loseGold(this.relicStream(BrilliantUp.class).mapToInt(BrilliantUp::getCounter).sum());
	}
	
	public void atBattleStart() {
		applyDamage();
		this.show();
    }
	
	public void onMonsterDeath(final AbstractMonster m) {
		this.gainTempGold();
		if (AbstractDungeon.getMonsters().areMonstersBasicallyDead() || AbstractDungeon.getMonsters().areMonstersDead())
			return;
		applyDamage();
		this.show();
    }

}