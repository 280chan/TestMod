package powers;

import java.util.ArrayList;
import java.util.HashMap;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import relics.TheFather;

public class TheFatherPower extends AbstractTestPower implements InvisiblePower {
	public static final String POWER_ID = "TheFatherPower";
	private static final int PRIORITY = 1000;
	private static final HashMap<AbstractMonster, ArrayList<DamageAction>> MAP =
			new HashMap<AbstractMonster, ArrayList<DamageAction>>();

	public static void reset() {
		MAP.clear();
	}
	
	public static boolean hasThis(AbstractCreature owner) {
		return owner.powers.stream().anyMatch(p -> p instanceof TheFatherPower);
	}
	
	public TheFatherPower(AbstractCreature owner) {
		super(POWER_ID);
		this.name = POWER_ID;
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
		this.addTmpActionToBot(() -> {
			r.count();
		});
	}
	
	private void addDamageAction(AbstractMonster m, int damage) {
		this.relicStream(TheFather.class).forEach(r -> {
			DamageAction a = new DamageAction(m, new DamageInfo(p(), damage, DamageType.THORNS), true);
			if (MAP.containsKey(m)) {
				MAP.get(m).add(a);
			} else {
				ArrayList<DamageAction> list = new ArrayList<DamageAction>();
				list.add(a);
				MAP.put(m, list);
			}
			this.addToBot(a);
			this.countAction(r);
		});
	}
	
	private static void clearDamageActions(AbstractMonster m) {
		if (MAP.containsKey(m)) {
			AbstractDungeon.actionManager.actions.removeAll(MAP.get(m));
			MAP.remove(m);
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
    
    static class Prime {
    	private static final ArrayList<Integer> PRIME = new ArrayList<Integer>();

    	static void clear() {
    		PRIME.clear();
    	}
    	
    	static int find(int n) {
    		if (n < 3)
    			return n + 1;
    		if (PRIME.size() == 0) {
    			PRIME.add(2);
    			PRIME.add(3);
    		}
    		for (int x = PRIME.get(PRIME.size() - 1); PRIME.size() < n;)
    			addIfIsPrime(x += 2);
    		return PRIME.get(n - 1);
    	}

    	private static void addIfIsPrime(int x) {
    		for (int i = 1; PRIME.get(i) <= (int) Math.sqrt(x) && i < PRIME.size(); i++)
    			if (x % PRIME.get(i) == 0)
    				return;
    		PRIME.add(x);
    	}

    	private static int indexOf(int start, int num) {
    		int size = PRIME.size();
    		if (size > 0 && PRIME.get(size - 1) >= num) {
    			for (int i = start; i < size; i++)
    				if (PRIME.get(i) == num)
    					return i + 1;
    			return -1;
    		}
    		if (size > 10000000)
    			find(10509756);
    		else if (size > 1000000)
    			find(size + 15000);
    		else if (size > 100000)
    			find(size + 1000);
    		else
    			find(Math.max(size * 2, 10000));
    		return indexOf(size, num);
    	}

    	static int indexOf(int num) {
    		return indexOf(0, num);
    	}

    	static boolean isPrime(int num) {
    		if (num < 2)
    			return false;
    		if (PRIME.size() < 100)
    			find(100);
    		for (int i = 0; PRIME.get(i) <= (int) Math.sqrt(num) && i < PRIME.size(); i++)
    			if (num % PRIME.get(i) == 0)
    				return false;
    		int preSize = PRIME.size();
    		if (PRIME.get(PRIME.size() - 1) <= (int) Math.sqrt(num)) {
    			indexOf((int) Math.sqrt(num));
        		for (int i = preSize; PRIME.get(i) <= (int) Math.sqrt(num) && i < PRIME.size(); i++)
        			if (num % PRIME.get(i) == 0)
        				return false;
    		}
    		return true;
    	}
    	
    	static ArrayList<Integer> primeFactorOf(int num) {
    		if (num < 2 || isPrime(num)) {
        		ArrayList<Integer> tmp = new ArrayList<Integer>();
    			tmp.add(num);
    			return tmp;
    		}
    		for (int i = 0, current = 2;; i++, current = PRIME.get(i))
    			if (num % current == 0)
    				return combine(current, primeFactorOf(num / current));
    	}
    	
    	private static ArrayList<Integer> combine(int a, ArrayList<Integer> b){
    		b.add(a);
    		return b;
    	}
    }
}
