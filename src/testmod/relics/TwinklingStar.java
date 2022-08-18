package testmod.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;

import testmod.utils.Star;

public class TwinklingStar extends AbstractTestRelic implements Star {
	public static final int STEP = 100;
	public static boolean lock = false;
	
	public TwinklingStar() {
		this.counter = 0;
	}
	
	public void onVictory() {
		if (this.isActive && lock)
			lock = false;
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0] + f() + DESCRIPTIONS[1];
	}
	
	private int f() {
		return this.counter / STEP + 1;
	}
	
	public void act() {
		if (this.hasEnemies() && !lock) {
			this.counter++;
			if (this.counter % STEP == STEP - 1) {
				this.beginLongPulse();
			} else if (this.counter % STEP == 0) {
				this.stopPulse();
				this.updateDescription();
			}
			this.flash();
			this.atb(new TwinklingStarDamageAction(f()));
		}
	}
	
	public static class TwinklingStarDamageAction extends AbstractGameAction {
		DamageAllEnemiesAction a;

		public TwinklingStarDamageAction(int dmg) {
			this(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(dmg, true), DamageType.THORNS,
					AttackEffect.LIGHTNING, true));
		}
		
		public TwinklingStarDamageAction(DamageAllEnemiesAction a) {
			this.a = a;
			this.actionType = ActionType.DAMAGE;
		}
		
		@Override
		public void update() {
			if (lock = !(this.isDone = a.isDone || !MISC.hasEnemies())) {
				a.update();
			}
		}
	}
	
}