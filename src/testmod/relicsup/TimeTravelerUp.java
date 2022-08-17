package testmod.relicsup;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import basemod.abstracts.CustomSavable;
import testmod.mymod.TestMod;
import testmod.utils.CounterKeeper;

public class TimeTravelerUp extends AbstractUpgradedRelic implements CounterKeeper, CustomSavable<Integer> {
	private static final double REST_SAN = 0.3;
	private static final int BOSS_SAN = 100;
	private static final int ELITE_SAN = 50;
	private static final int MONSTER_SAN = 10;
	private static final int SAN_TO_GAIN_ENERGY = 100;
	public static final String SAVE_NAME = "TTUDelta";
	private static int delta = 1;
	private static boolean loading = false;

	public void run(AbstractRelic r, AbstractUpgradedRelic u) {
		u.counter = r.counter + 100;
		u.updateDescription();
	}

	@Override
	public void onLoad(Integer saved) {
		if (TestMod.config == null)
			TestMod.initSavingConfig();
		delta = TestMod.getInt(SAVE_NAME);
		if (saved != null) {
			this.counter = Math.max(saved + delta, 0);
		}
		if (!loading) {
			TestMod.save(SAVE_NAME, ++delta);
			loading = true;
			this.addTmpEffect(() -> loading = false);
		}
	}

	@Override
	public Integer onSave() {
		TestMod.save(SAVE_NAME, delta = 1);
		return this.counter;
	}
	
	public void atTurnStart() {
		int i = 0;
		while (this.counter >> i >= SAN_TO_GAIN_ENERGY) {
			i++;
		}
		if (i > 0) {
			this.atb(new GainEnergyAction(i));
			this.atb(new DrawCardAction(i));
			this.show();
		}
	}
	
	public void onEquip() {
		this.counter = 100;
    }
	
	public void onUnequip() {
		TimeTravelerUp t = this.relicStream(TimeTravelerUp.class).filter(r -> !r.isActive).findFirst().orElse(null);
		if (t != null)
			t.counter += this.counter;
    }
	
	public void onMonsterDeath(AbstractMonster m) {
		int a = MONSTER_SAN;
		switch (m.type) {
		case BOSS:
			a = BOSS_SAN;
			break;
		case ELITE:
			a = ELITE_SAN;
		default:
			break;
		}
		this.counter += a;
	}
	
	public void onRest() {
		this.counter += REST_SAN * this.counter;
    }
}