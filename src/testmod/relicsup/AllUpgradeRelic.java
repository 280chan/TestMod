package testmod.relicsup;

import relicupgradelib.arch.*;
import testmod.relics.*;

public class AllUpgradeRelic {
	
	private static class Register {
		private static Proxy p;
		private static void set(AbstractTestRelic r, AbstractTestRelic r1, boolean red, boolean green, boolean blue,
				int gold) {
			p = new Proxy(r);
			p.addBranch(new UpgradeBranch(r1, null, red, green, blue, gold));
			ProxyManager.register(p);
		}
		private static void set(AbstractTestRelic r, AbstractTestRelic r1, boolean red, boolean green, boolean blue) {
			set(r, r1, red, green, blue, 0);
		}
		private static void set(AbstractTestRelic r, AbstractTestRelic r1, int gold) {
			set(r, r1, false, false, false, gold);
		}
	}
	
	public static void add() {
		Register.set(new TestBox(), new TestBoxUp(), 99);
		
		
	}
	
	
}
