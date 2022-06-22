package testmod.utils;

import com.megacrit.cardcrawl.relics.AbstractRelic;

import testmod.relicsup.AbstractUpgradedRelic;

public interface CounterKeeper {
	public default void run(AbstractRelic r, AbstractUpgradedRelic u) {
		u.counter = r.counter;
		u.updateDescription();
	}
}
