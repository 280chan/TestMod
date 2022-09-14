package testmod.utils;

import java.util.ArrayList;
import java.util.stream.Stream;

import testmod.relics.PatchyPatch;
import testmod.relicsup.PatchyPatchUp;

public interface PatchyTrigger {
	public static final PatchyTrigger[] CURRENT = new PatchyTrigger[1];
	
	public static Stream<PatchyTrigger> get() {
		return MiscMethods.MISC.p().relics.stream().filter(r -> r instanceof PatchyTrigger).map(r -> (PatchyTrigger) r);
	}
	
	public static void load() {
		PatchyPatchUp.RELICS.clear();
		PatchyPatch.RELICS.clear();
		ArrayList<PatchyTrigger> l = get().collect(MiscMethods.MISC.toArrayList());
		l.stream().filter(r -> r instanceof PatchyPatchUp).forEach(r -> PatchyPatchUp.RELICS.add((PatchyPatchUp) r));
		l.stream().filter(r -> r instanceof PatchyPatch).forEach(r -> PatchyPatch.RELICS.add((PatchyPatch) r));
		l.clear();
		CURRENT[0] = (PatchyPatchUp.RELICS.isEmpty() ? PatchyPatch.RELICS : PatchyPatchUp.RELICS).stream().findFirst()
				.orElse(null);
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
