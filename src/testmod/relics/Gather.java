package testmod.relics;

import java.util.ArrayList;
import java.util.HashMap;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import testmod.relicsup.GatherUp;
import testmod.screens.GatherSelectScreen;

public class Gather extends AbstractTestRelic {
	
	public static int f(AbstractRelic r) {
		return r.description.length();
	}
	
	public static boolean valid() {
		return MISC.replicaRelicStream().mapToInt(r -> f(r)).distinct().count() < MISC.p().relics.size();
	}
	
	public static void openScreen(boolean upgrade) {
		HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
		MISC.replicaRelicStream().forEach(r -> tmp.compute(f(r), (a, b) -> b == null ? 1 : b + 1));
		ArrayList<AbstractRelic> l = MISC.replicaRelicStream().filter(r -> tmp.get(f(r)) > 1).collect(MISC.toArrayList());
		tmp.clear();
		new GatherSelectScreen(l, l.stream().mapToInt(r -> f(r)).distinct().count() > 1, upgrade).open();
	}
	
	public void atPreBattle() {
		if (this.isActive && this.relicStream(GatherUp.class).count() == 0 && valid()) {
			openScreen(false);
			this.flash();
		}
	}

}