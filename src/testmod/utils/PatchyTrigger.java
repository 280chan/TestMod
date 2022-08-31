package testmod.utils;

import testmod.relics.AbstractTestRelic;

public interface PatchyTrigger {
	default void patchAttack(String patch) {
		if (MiscMethods.MISC.stackTrace().filter(e -> PatchyTrigger.class.getCanonicalName().equals(e.getClassName())
				&& "patchAttack".equals(e.getMethodName())).count() > 1)
			return;
		if (MiscMethods.MISC.p() == null || !MiscMethods.MISC.inCombat()
				|| MiscMethods.MISC.p().relics.stream().filter(r -> r instanceof PatchyTrigger).count() == 0)
			return;
		MiscMethods.MISC.p().relics.stream().filter(r -> r instanceof PatchyTrigger && ((AbstractTestRelic) r).isActive)
				.forEach(r -> ((PatchyTrigger) r).realAttack(patch));
	}
	
	void realAttack(String patch);
}
