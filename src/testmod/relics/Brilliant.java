package testmod.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Brilliant extends AbstractTestRelic {
	
	private static int damageFunction(int gold) {
		return (int) (Math.cbrt(gold) * Math.cbrt(gold) / 2);
	}
	
	private void applyDamage() {
		int[] dmg = DamageInfo.createDamageMatrix(damageFunction(p().gold), true);
		this.atb(new DamageAllEnemiesAction(null, dmg, DamageType.THORNS, AttackEffect.BLUNT_LIGHT));
	}
	
	public void atBattleStart() {
		applyDamage();
		this.show();
    }
	
	public void onMonsterDeath(final AbstractMonster m) {
		if (AbstractDungeon.getMonsters().areMonstersBasicallyDead() || AbstractDungeon.getMonsters().areMonstersDead())
			return;
		applyDamage();
		this.show();
    }

}