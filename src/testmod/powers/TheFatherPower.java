package testmod.powers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import testmod.relics.TwinklingStar;
import testmod.relicsup.TheFatherUp;

public class TheFatherPower extends AbstractTestPower implements InvisiblePower {
	private static final int PRIORITY = 1000;
	private static final HashMap<AbstractMonster, ArrayList<DamageAction>> ACTION_MAP =
			new HashMap<AbstractMonster, ArrayList<DamageAction>>();
	private static final ArrayList<DamageInfo> INFO = new ArrayList<DamageInfo>();

	public static void reset() {
		ACTION_MAP.values().forEach(l -> l.clear());
		ACTION_MAP.clear();
		INFO.clear();
	}
	
	public static boolean needThis(AbstractCreature owner) {
		return owner.powers.stream().noneMatch(p -> p instanceof TheFatherPower);
	}
	
	private boolean noUp() {
		return this.relicStream(TheFatherUp.class).count() == 0;
	}
	
	public TheFatherPower(AbstractCreature owner) {
		this.owner = owner;
		updateDescription();
		this.type = PowerType.DEBUFF;
		this.priority = PRIORITY;
		this.addMap(p -> new TheFatherPower(p.owner));
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	public static interface TheFatherCounter {
		void count();
	}
	
	private Stream<TheFatherCounter> stream() {
		return p().relics.stream().filter(r -> r instanceof TheFatherCounter).map(r -> (TheFatherCounter) r);
	}
	
	private void addDamageAction(AbstractMonster m, int damage) {
		stream().forEach(r -> {
			DamageInfo info = new DamageInfo(p(), damage, DamageType.THORNS);
			INFO.add(info);
			DamageAction a = new DamageAction(m, info, true);
			if (ACTION_MAP.containsKey(m)) {
				ACTION_MAP.get(m).add(a);
			} else {
				ArrayList<DamageAction> list = new ArrayList<DamageAction>();
				list.add(a);
				ACTION_MAP.put(m, list);
			}
			this.atb(a);
			this.addTmpActionToBot(r::count);
			TwinklingStar.addTheFatherAction();
		});
	}
	
	private static void clearDamageActions(AbstractMonster m) {
		if (ACTION_MAP.containsKey(m)) {
			AbstractDungeon.actionManager.actions.removeAll(ACTION_MAP.get(m));
			ACTION_MAP.remove(m);
		}
	}
	
	public int onAttacked(final DamageInfo info, int damage) {
		if (INFO.remove(info) && this.noUp())
			return damage;
		if (Prime.isPrime(damage)) {
			AbstractDungeon.getMonsters().monsters.stream().filter(m -> !(m.equals(this.owner) || m.isDeadOrEscaped()))
					.forEach(m -> this.addDamageAction(m, Prime.indexOf(damage)));
		} else if (damage > 3) {
			Prime.primeFactorOf(damage)
					.forEach(p -> this.addDamageAction((AbstractMonster) this.owner, Prime.indexOf(p)));
		}
		return damage;
	}
	
	public void onDeath() {
		clearDamageActions((AbstractMonster) this.owner);
	}

	public static void clear() {
		Prime.clear();
	}
}
