package relics;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import mymod.TestMod;
import powers.OneHitWonderDebuffPower;

public class OneHitWonder extends AbstractTestRelic {
	
	private static ArrayList<AbstractMonster> queue;
	
	private boolean getRoll() {
		return AbstractDungeon.cardRandomRng.randomBoolean();
	}
	
	public OneHitWonder() {
		super(RelicTier.COMMON, LandingSound.MAGICAL, BAD);
	}
	
	public void atPreBattle() {
		if (this.isActive)
			queue.clear();
		this.controlPulse();
		if (AbstractDungeon.currMapNode == null)
			return;
		this.tryApplyDebuff();
    }
	
	private void tryApplyDebuff() {
		if (!this.hasEnemies())
			return;
		AbstractDungeon.getMonsters().monsters.stream()
				.filter(m -> m.type == EnemyType.BOSS && !OneHitWonderDebuffPower.hasThis(m))
				.forEach(m -> m.powers.add(new OneHitWonderDebuffPower(m)));
	}
	
	public void update() {
		super.update();
		this.tryApplyDebuff();
	}
	
	public int onPlayerHeal(final int healAmount) {
		this.controlPulse();
        return healAmount;
    }
	
	public void onAttack(final DamageInfo info, final int damageAmount, final AbstractCreature target) {
		if (AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom().phase != RoomPhase.COMBAT)
			return;
		if (isActive() && info.type == DamageType.NORMAL && target != null && !target.isPlayer) {
			AbstractMonster m = (AbstractMonster)target;
			if (!queue.contains(m) && m.type != EnemyType.BOSS && getRoll()) {
				TestMod.info("一血传奇：条件满足，准备秒杀" + m.name);
				queue.add(m);
				this.addTmpActionToTop(() -> {
					if (m.isDeadOrEscaped() || m.escaped) {
						TestMod.info("一血传奇：秒杀失败，" + m.name + "已死亡或逃跑");
						return;
					}
					this.show();
					this.addToTop(new InstantKillAction(m));
				});
			}
		}
	}
	
	public void onLoseHp(int damageAmount) {
		controlPulse();
	}

	private void controlPulse() {
		if (isActive()) {
		    beginLongPulse();
		} else {
		    stopPulse();
		}
	}
	
	private boolean isActive() {
		return p().currentHealth == 1;
	}
	
}