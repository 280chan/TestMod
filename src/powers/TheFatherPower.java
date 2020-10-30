package powers;

import java.util.ArrayList;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

import relics.TheFather;

public class TheFatherPower extends AbstractTestPower implements InvisiblePower {
	public static final String POWER_ID = "TheFatherPower";
	private static final int PRIORITY = 1000;
	private TheFather relic;
	
	public static boolean hasThis(AbstractCreature owner) {
		for (AbstractPower p : owner.powers)
			if (p instanceof TheFatherPower)
				return true;
		return false;
	}
	
	public TheFatherPower(AbstractCreature owner, TheFather relic) {
		super(POWER_ID);
		this.name = POWER_ID;
		this.owner = owner;
		updateDescription();
		this.type = PowerType.DEBUFF;
		this.priority = PRIORITY;
		this.relic = relic;
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
	public void onRemove() {
		this.addToTop(new AbstractGameAction() {
			@Override
			public void update() {
				this.isDone = true;
				if (!hasThis(TheFatherPower.this.owner))
					TheFatherPower.this.owner.powers
							.add(new TheFatherPower(TheFatherPower.this.owner, TheFatherPower.this.relic));
			}
		});
	}
	
	private void countAction() {
		this.addToBot(new AbstractGameAction() {
			@Override
			public void update() {
				this.isDone = true;
				TheFatherPower.this.relic.count();
			}
		});
	}
	
    public int onAttacked(final DamageInfo info, int damage) {
    	if (Prime.isPrime(damage)) {
    		for (AbstractCreature m : AbstractDungeon.getCurrRoom().monsters.monsters) {
    			if (!m.equals(this.owner)) {
    				this.addToBot(new DamageAction(m, new DamageInfo(AbstractDungeon.player, Prime.indexOf(damage), DamageType.THORNS)));
    				this.countAction();
    			}
    		}
    	} else if (damage > 3) {
    		for (int p : Prime.primeFactorOf(damage)) {
    			this.addToBot(new DamageAction(this.owner, new DamageInfo(AbstractDungeon.player, Prime.indexOf(p), DamageType.THORNS)));
    			this.countAction();
    		}
    	}
		return damage;
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
    		ArrayList<Integer> tmp = new ArrayList<Integer>();
    		if (num < 2 || isPrime(num)) {
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
