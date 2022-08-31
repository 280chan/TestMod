package testmod.relics;

import java.util.ArrayList;
import java.util.stream.Stream;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import testmod.powers.AbstractTestPower;

public class InjuryResistance extends AbstractTestRelic {
	
	public void onEquip() {
		this.counter = 0;
	}
	
	private void addPower() {
		if (this.isActive && needThis())
			this.addPower(new InjuryResistancePower());
	}
	
	public void justEnteredRoom(AbstractRoom r)	{
		this.addPower();
	}
	
	public void atPreBattle() {
		this.addPower();
    }
	
	private void counterPP() {
		if (this.counter > 0)
			this.show();
		this.counter++;
	}
	
	private boolean needThis() {
		return p().powers.stream().noneMatch(p -> p instanceof InjuryResistancePower);
	}
	
	public static class InjuryResistancePower extends AbstractTestPower implements InvisiblePower {
		private static final int PRIORITY = 1000000;
		
		public InjuryResistancePower() {
			this.owner = p();
			updateDescription();
			this.type = PowerType.BUFF;
			this.priority = PRIORITY;
			this.addMapWithSkip(p -> new InjuryResistancePower());;
		}
		
		public void updateDescription() {
			 this.description = "";
		}
		
		public void stackPower(final int stackAmount) {
			this.fontScale = 8.0f;
		}
		
		private Stream<InjuryResistance> stream() {
			return this.relicStream(InjuryResistance.class);
		}
		
	    public int onLoseHp(int damage) {
	    	if (damage < 1)
	    		return damage;
	    	int tmp = stream().mapToInt(r -> r.counter).sum();
	    	if (damage > tmp) {
	    		stream().forEach(r -> r.counterPP());
	    		return damage - tmp;
			}
			ArrayList<InjuryResistance> l = stream().sorted((a, b) -> a.counter - b.counter).collect(toArrayList());
			for (InjuryResistance r : l) {
				if (r.counter > 0)
					r.show();
				damage -= r.counter++;
				if (damage <= 0) {
					break;
				}
			}
			l.clear();
	        return 0;
	    }
	    
	}

}