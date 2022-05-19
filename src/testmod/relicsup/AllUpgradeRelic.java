package testmod.relicsup;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

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
	}
	
	public static void add(AbstractTestRelic r) {
		String original = r.getClass().getSimpleName();
		UIStrings tmp = CardCrawlGame.languagePack.getUIString(UIID(original));
		int key = Integer.parseInt(tmp.TEXT[0]);
		int gold = Integer.parseInt(tmp.TEXT[1]);
		// blue * 4 + green * 2 + red
		boolean red = key % 2 == 1;
		boolean green = (key % 4) / 2 == 1;
		boolean blue = key / 4 == 1;
		AbstractUpgradedRelic up = upgrade(original);
		if (up != null)
			Register.set(r, up, red, green, blue, gold);
	}
	
	private static String UIID(String original) {
		return "testmod-upgrade-" + original;
	}
	
	private static String fullName(String original) {
		return "testmod.relicsup." + original + "Up";
	}
	
	public static AbstractUpgradedRelic upgrade(String original) {
		try {
			return (AbstractUpgradedRelic) Class.forName(fullName(original)).newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			return null;
		}
	}
	
}
