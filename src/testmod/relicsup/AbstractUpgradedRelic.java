package testmod.relicsup;

import com.megacrit.cardcrawl.vfx.FloatyEffect;

import basemod.ReflectionHacks;
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
	
	public void bossObtainLogic() {
		if (this.tier != RelicTier.BOSS)
			return;
		int index = p().relics.indexOf(p().getRelic(removePostfixForIMG(this.relicId)));
		this.instantObtain(p(), index != -1 ? index : p().relics.size(), true);
		this.flash();
		FloatyEffect fe = ReflectionHacks.getPrivateInherited(this, getClass(), "f_effect");
		fe.x = fe.y = 0.0F;
	}

}
