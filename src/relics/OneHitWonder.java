package relics;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import powers.OneHitWonderDebuffPower;

public class OneHitWonder extends AbstractTestRelic{
	public static final String ID = "OneHitWonder";
	
	private boolean getRoll() {
		return AbstractDungeon.cardRng.randomBoolean();
	}
	
	public OneHitWonder() {
		super(ID, RelicTier.COMMON, LandingSound.MAGICAL);
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
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			boolean had = false;
			for (AbstractPower p : m.powers)
				if (p instanceof OneHitWonderDebuffPower)
					had = true;
			if (m.type == EnemyType.BOSS && !had) {
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
				this.show();
				m.currentHealth = 0;
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