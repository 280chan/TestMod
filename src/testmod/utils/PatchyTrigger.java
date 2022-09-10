package testmod.utils;

import java.util.ArrayList;

import testmod.relics.AbstractTestRelic;

public interface PatchyTrigger {
	public static final ArrayList<PatchyTrigger> LIST = new ArrayList<PatchyTrigger>();
	public static final PatchyTrigger PT = new PatchyTrigger() {
		@Override
		public void realAttack(String patch) {
		}
	};
	
	public static void load() {
		LIST.clear();
		MiscMethods.MISC.p().relics.stream().filter(r -> r instanceof PatchyTrigger && ((AbstractTestRelic) r).isActive)
				.forEach(r -> LIST.add((PatchyTrigger) r));
	}
	
	public static boolean valid() {
		return !LIST.isEmpty();
	}
	
	default void patchAttack(String patch) {
		if (MiscMethods.MISC.stackTrace().filter(e -> PatchyTrigger.class.getCanonicalName().equals(e.getClassName())
				&& "patchAttack".equals(e.getMethodName())).count() > 1)
			return;
		if (MiscMethods.MISC.p() == null || !MiscMethods.MISC.inCombat()
				|| MiscMethods.MISC.p().relics.stream().filter(r -> r instanceof PatchyTrigger).count() == 0)
			return;
		LIST.forEach(r -> r.realAttack(patch));
	}
	
	void realAttack(String patch);
}
