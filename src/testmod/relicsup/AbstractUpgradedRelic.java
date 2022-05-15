package testmod.relicsup;

import testmod.mymod.TestMod;
import testmod.relics.AbstractTestRelic;

public abstract class AbstractUpgradedRelic extends AbstractTestRelic {

	private static String removePostfixForIMG(String id) {
		return id.endsWith("Up") ? id.substring(0, id.length() - 2) : id;
	}
	
	private AbstractUpgradedRelic(String id, RelicTier tier, LandingSound sfx) {
		super(TestMod.makeID(id), TestMod.relicIMGPath(removePostfixForIMG(id)), tier, sfx);
	}
	
	public AbstractUpgradedRelic(RelicTier tier, LandingSound sfx) {
		this(shortID(getRelicClass()), tier, sfx);
	}

}
