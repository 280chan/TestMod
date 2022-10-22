package testmod.relicsup;

import java.util.ArrayList;
import java.util.stream.Stream;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import testmod.powers.AbstractTestPower;
import testmod.utils.CounterKeeper;

public class InjuryResistanceUp extends AbstractUpgradedRelic implements CounterKeeper {
	
	public void onEquip() {
		if (!this.hasStack("relicupgradelib.ui.RelicUpgradePopup", "replaceRelic")) {
			this.counter = 0;
		}
	}
	
	private void addPower() {
		if (needThis())
			this.addPower(new InjuryResistancePowerUp());
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
		return p().powers.stream().noneMatch(p -> p instanceof InjuryResistancePowerUp);
	}
	
	public static class InjuryResistancePowerUp extends AbstractTestPower implements InvisiblePower {
		private static final int PRIORITY = 1000000;
		
		public InjuryResistancePowerUp() {
			this.owner = p();
			updateDescription();
			this.type = PowerType.BUFF;
			this.priority = PRIORITY;
			this.addMapWithSkip(p -> new InjuryResistancePowerUp());;
		}
		
		public void updateDescription() {
			 this.description = "";
		}
		
		public void stackPower(final int stackAmount) {
			this.fontScale = 8.0f;
		}
		
		private Stream<InjuryResistanceUp> stream() {
			return this.relicStream(InjuryResistanceUp.class);
		}
		
		public int onLoseHp(int damage) {
			if (damage < 1)
				return damage;
			int tmp = stream().mapToInt(r -> r.counter).sum();
			if (damage >= tmp) {
				stream().forEach(r -> r.counterPP());
				return damage - tmp;
			}
			ArrayList<InjuryResistanceUp> l = stream().sorted((a, b) -> a.counter - b.counter).collect(toArrayList());
			boolean stop = false;
			for (InjuryResistanceUp r : l) {
				if (r.counter > 0)
					r.show();
				damage -= r.counter++;
				if (!stop)
					r.counter /= 2;
				stop |= damage < 0;
			}
			l.clear();
			return 0;
		}
	}
}