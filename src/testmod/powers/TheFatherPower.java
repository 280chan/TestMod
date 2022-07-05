package testmod.powers;

import java.util.ArrayList;
import java.util.HashMap;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import testmod.relics.TheFather;

public class TheFatherPower extends AbstractTestPower implements InvisiblePower {
	private static final int PRIORITY = 1000;
	private static final HashMap<AbstractMonster, ArrayList<DamageAction>> ACTION_MAP =
			new HashMap<AbstractMonster, ArrayList<DamageAction>>();

	public static void reset() {
		ACTION_MAP.clear();
	}
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof TheFatherPower);
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
	
	private void countAction(TheFather r) {
		this.addTmpActionToBot(() -> r.count());
	}
	
	private void addDamageAction(AbstractMonster m, int damage) {
		this.relicStream(TheFather.class).forEach(r -> {
			DamageAction a = new DamageAction(m, new DamageInfo(p(), damage, DamageType.THORNS), true);
			if (ACTION_MAP.containsKey(m)) {
				ACTION_MAP.get(m).add(a);
			} else {
				ArrayList<DamageAction> list = new ArrayList<DamageAction>();
				list.add(a);
				ACTION_MAP.put(m, list);
			}
			this.addToBot(a);
			this.countAction(r);
		});
	}
	
	private static void clearDamageActions(AbstractMonster m) {
		if (ACTION_MAP.containsKey(m)) {
			AbstractDungeon.actionManager.actions.removeAll(ACTION_MAP.get(m));
			ACTION_MAP.remove(m);
		}
	}
	
    public int onAttacked(final DamageInfo info, int damage) {
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
    
    public static boolean isPrime(int n) {
    	return Prime.isPrime(n);
    }
    
    public static int indexOf(int p) {
    	return Prime.indexOf(p);
    }
}
