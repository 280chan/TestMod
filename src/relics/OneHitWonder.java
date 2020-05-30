package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import mymod.TestMod;
import powers.OneHitWonderDebuffPower;

public class OneHitWonder extends MyRelic{
	public static final String ID = "OneHitWonder";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "当你的生命值为 #b1 时：攻击有一半几率可以直接杀死被击中的非Boss敌人；Boss敌人造成的伤害降低 #b50% ，受到的攻击伤害和未被格挡的其余伤害增加 #b50% 。";//遗物效果的文本描叙。
	
	private boolean getRoll() {
		return AbstractDungeon.cardRng.randomBoolean();
	}
	
	public OneHitWonder() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.UNCOMMON, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atPreBattle() {
		if (!isActive)
			return;
		this.controlPulse();
		this.tryApplyDebuff();
    }
	
	public void atTurnStart() {
		this.tryApplyDebuff();
	}
	
	private void tryApplyDebuff() {
		boolean applied = false;
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (m.type == EnemyType.BOSS && !m.hasPower(OneHitWonderDebuffPower.POWER_ID)) {
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
	
	public void onAttack(final DamageInfo info, final int damageAmount, final AbstractCreature target) {//参数： info-伤害信息，damageAmount-伤害数值，target-伤害目标
		if (!isActive)
			return;
		if (AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom().phase != RoomPhase.COMBAT)
			return;
		if (isActive() && info.type == DamageType.NORMAL && target != null) {
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