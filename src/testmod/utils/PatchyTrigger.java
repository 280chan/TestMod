package testmod.utils;

import java.util.stream.Stream;
import testmod.relicsup.PatchyPatchUp;

public interface PatchyTrigger {
	public static final PatchyTrigger[] CURRENT = new PatchyTrigger[1];
	
	public static Stream<PatchyTrigger> get() {
		return MiscMethods.MISC.p().relics.stream().filter(r -> r instanceof PatchyTrigger).map(r -> (PatchyTrigger) r);
	}
	
	public static void load() {
		CURRENT[0] = get().findFirst().orElse(null);
		if (CURRENT[0] instanceof PatchyPatchUp)
			return;
		CURRENT[0] = get().filter(r -> r instanceof PatchyPatchUp).findFirst().orElse(CURRENT[0]);
	}
	
	public static boolean valid() {
		return CURRENT[0] != null;
	}
	
	public static void patchAttack(String patch) {
		if (MiscMethods.MISC.stackTrace().filter(e -> PatchyTrigger.class.getCanonicalName().equals(e.getClassName())
				&& "patchAttack".equals(e.getMethodName())).count() > 1)
			return;
		CURRENT[0].realAttack(patch);
	}
	
	void realAttack(String patch);
}
