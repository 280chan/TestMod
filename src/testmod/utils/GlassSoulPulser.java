package testmod.utils;

import java.util.ArrayList;

import testmod.relics.AbstractTestRelic;

public interface GlassSoulPulser {
	public static final ArrayList<String> RELICS = new ArrayList<String>(), TMP = new ArrayList<String>();
	
	default void tryPulse(boolean skip) {
		AbstractTestRelic r = (AbstractTestRelic) this;
		if ((skip || !r.inCombat()) && r.isActive && !RELICS.isEmpty() && RELICS.stream().anyMatch(this::canBuy))
			r.beginLongPulse();
	}
	
	boolean canBuy(String id);
}
