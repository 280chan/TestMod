package testmod.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;

public class TwinklingStar extends AbstractTestRelic {
	public static final int STEP = 128;
	private static boolean lock = false;
	
	public TwinklingStar() {
		super(RelicTier.COMMON, LandingSound.MAGICAL);
		this.counter = 0;
	}
	
	public void onVictory() {
		if (this.isActive && lock)
			lock = false;
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0] + f() + DESCRIPTIONS[1] + (counter - (counter % STEP) + STEP) + DESCRIPTIONS[2];
	}
	
	private int f() {
		if (this.counter > (31 * STEP - 1)) {
			return 2000000000;
		}
		return this.getIdenticalList(2, this.counter / STEP).stream().reduce(1, (a, b) -> a * b);
	}
	
	public void act() {
		if (this.hasEnemies() && !lock) {
			this.counter++;
			if (this.counter % STEP == STEP - 1) {
				this.beginLongPulse();
			} else if (this.counter % STEP == 0) {
				this.stopPulse();
				this.updateDescription(p().chosenClass);
			}
			this.flash();
			this.atb(new TwinklingStarDamageAction(new DamageAllEnemiesAction(null,
					DamageInfo.createDamageMatrix(f(), true), DamageType.THORNS, AttackEffect.LIGHTNING, true)));
		}
	}
	
	private static class TwinklingStarDamageAction extends AbstractGameAction {
		DamageAllEnemiesAction a;
		private TwinklingStarDamageAction(DamageAllEnemiesAction a) {
			this.a = a;
			this.actionType = ActionType.DAMAGE;
		}
		@Override
		public void update() {
			if (lock = !(this.isDone = a.isDone)) {
				a.update();
			}
		}
	}
	
}