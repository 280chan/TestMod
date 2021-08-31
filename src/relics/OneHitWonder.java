package relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
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
import utils.MiscMethods;

public class OneHitWonder extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "OneHitWonder";
	
	private boolean getRoll() {
		return AbstractDungeon.cardRng.randomBoolean();
	}
	
	public OneHitWonder() {
		super(ID, RelicTier.COMMON, LandingSound.MAGICAL);
		this.setTestTier(BAD);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atPreBattle() {
		if (!isActive)
			return;
		this.controlPulse();
		if (AbstractDungeon.currMapNode == null) {
			return;
		}
		this.tryApplyDebuff();
    }
	
	public void atTurnStart() {
		this.tryApplyDebuff();
	}
	
	private void tryApplyDebuff() {
		boolean applied = false;
		if (!this.hasEnemies())
			return;
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (m.type == EnemyType.BOSS && !OneHitWonderDebuffPower.hasThis(m)) {
				m.powers.add(new OneHitWonderDebuffPower(m));
				applied = true;
			}
		}
		if (applied && this.isActive())
			this.show();
	}
	
	public int onPlayerHeal(final int healAmount) {
		if (!isActive)
			return healAmount;
		this.controlPulse();
        return healAmount;
    }
	
	public void onAttack(final DamageInfo info, final int damageAmount, final AbstractCreature target) {
		if (!isActive)
			return;
		if (AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom().phase != RoomPhase.COMBAT)
			return;
		if (isActive() && info.type == DamageType.NORMAL && target != null && !target.isPlayer) {
			AbstractMonster m = (AbstractMonster)target;
			if (m.type != EnemyType.BOSS && getRoll()) {
				TestMod.info("一血传奇：条件满足，准备秒杀" + m.name);
				this.addToTop(new AbstractGameAction() {
					@Override
					public void update() {
						this.isDone = true;
						if (m.isDeadOrEscaped() || m.escaped) {
							TestMod.info("一血传奇：秒杀失败，" + m.name + "已死亡或逃跑");
							return;
						}
						OneHitWonder.this.show();
						this.addToTop(new InstantKillAction(m));
					}
				});
			}
		}
	}
	
	public void onLoseHp(int damageAmount) {
		if (!isActive)
			return;
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
		return AbstractDungeon.player.currentHealth == 1;
	}
	
}