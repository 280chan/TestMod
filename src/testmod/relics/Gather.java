package testmod.relics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import testmod.screens.GatherSelectScreen;

public class Gather extends AbstractTestRelic {
	
	public Gather() {
		super(RelicTier.RARE, LandingSound.MAGICAL, GOD);
	}
	
	public static int f(AbstractRelic r) {
		return r.description.length();
	}
	
	public static boolean valid() {
		return INSTANCE.replicaRelicStream().mapToInt(r -> f(r)).distinct().count() < INSTANCE.p().relics.size();
	}
	
	public void openScreen() {
		HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
		Consumer<Integer> c = n -> {
			if (tmp.containsKey(n))
				tmp.replace(n, tmp.get(n) + 1);
			else
				tmp.put(n, 1);
		};
		replicaRelicStream().forEach(r -> c.accept(f(r)));
		ArrayList<AbstractRelic> l = replicaRelicStream().filter(r -> tmp.get(f(r)) > 1).collect(toArrayList());
		tmp.clear();
		new GatherSelectScreen(l, l.stream().mapToInt(r -> f(r)).distinct().count() > 1, "").open();
	}
	
	
	public void atPreBattle() {
		if (this.isActive && valid()) {
			openScreen();
			this.flash();
		}
    }
}