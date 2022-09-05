package testmod.relics;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import testmod.powers.TheFatherPower.TheFatherCounter;
import testmod.relicsup.TheFatherUp;
import testmod.utils.Star;

public class TwinklingStar extends AbstractTestRelic implements Star {
	public static final int STEP = 100;
	public static boolean lock = false;
	public static ArrayList<AbstractGameAction> theFather = new ArrayList<AbstractGameAction>();
	private static final String[] FATHER_STACK = { TheFather.class.getCanonicalName(),
			TheFatherUp.class.getCanonicalName(), TheFatherCounter.class.getCanonicalName() };

	public static void clearTheFatherAction() {
		theFather.clear();
	}
	
	public static void addTheFatherAction() {
		if (lock) {
			theFather.add(AbstractDungeon.actionManager.actions.get(AbstractDungeon.actionManager.actions.size() - 1));
		}
	}
	
	public static boolean checkTheFatherAction() {
		return theFather.contains(AbstractDungeon.actionManager.currentAction)
				&& MISC.hasStack(GameActionManager.class.getCanonicalName(), "update")
				&& Stream.of(FATHER_STACK).anyMatch(s -> MISC.hasStack(s, "count"));
	}
	
	public TwinklingStar() {
		this.counter = 0;
	}
	
	public void onVictory() {
		if (this.isActive && lock)
			lock = false;
		clearTheFatherAction();
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0] + f() + DESCRIPTIONS[1];
	}
	
	private int f() {
		return this.counter / STEP + 1;
	}
	
	public void act() {
		if (this.hasEnemies() && !lock && !checkTheFatherAction()) {
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