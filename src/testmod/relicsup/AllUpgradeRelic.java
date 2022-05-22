package testmod.relicsup;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import relicupgradelib.arch.*;
import testmod.relics.*;

public class AllUpgradeRelic {
	
	private static class Register {
		private static Proxy p;
		private static void set(AbstractTestRelic r, boolean red, boolean green, boolean blue,
				int gold) {
			p.addBranch(new UpgradeBranch(r, null, red, green, blue, gold));
			ProxyManager.register(p);
		}
		private static void init(AbstractTestRelic r) {
			p = new Proxy(r);
		}
	}
	
	public static void add(AbstractTestRelic r) {
		String original = r.getClass().getSimpleName();
		AbstractUpgradedRelic up = upgrade(original);
		if (up != null) {
			Register.init(r);
			UIStrings tmp = CardCrawlGame.languagePack.getUIString(UIID(original));
			for (int i = 0; i < tmp.TEXT.length; i += 2) {
				int key = Integer.parseInt(tmp.TEXT[i]);
				int gold = Integer.parseInt(tmp.TEXT[i + 1]);
				// blue * 4 + green * 2 + red
				boolean red = key % 2 == 1;
				boolean green = (key % 4) / 2 == 1;
				boolean blue = key / 4 == 1;
				Register.set(up, red, green, blue, gold);
			}
		}
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
	
	public static AbstractRelic getUpgrade(AbstractRelic r) {
		return ProxyManager.getProxyByRelic(r).braches.get(0).relic;
	}
	
	public static boolean canUpgrade(AbstractRelic r) {
		return ProxyManager.getProxyByRelic(r) != null;
	}
	
}
