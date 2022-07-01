package testmod.relicsup;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.FloatyEffect;

import basemod.ReflectionHacks;
import testmod.mymod.TestMod;
import testmod.relics.AbstractTestRelic;
import testmod.utils.CounterKeeper;

public abstract class AbstractUpgradedRelic extends AbstractTestRelic {
	protected int upgradeTimes = 1;

	private static String removePostfixForIMG(String id) {
		return id.endsWith("Up") ? id.substring(0, id.length() - 2) : id;
	}
	
	private AbstractUpgradedRelic(String id, RelicTier tier, LandingSound sfx) {
		super(TestMod.makeID(id), TestMod.relicIMGPath(removePostfixForIMG(id)), tier, sfx);
	}
	
	public AbstractUpgradedRelic(RelicTier tier, LandingSound sfx) {
		this(shortID(getRelicClass()), tier, sfx);
	}
	
	public AbstractUpgradedRelic(RelicTier tier, LandingSound sfx, int upgradeTimes) {
		this(tier, sfx);
		this.upgradeTimes = upgradeTimes;
	}
	
	public void bossObtainLogic() {
		if (this.tier != RelicTier.BOSS)
			return;
		AbstractRelic r = p().getRelic(removePostfixForIMG(this.relicId));
		if (r != null)
			r.onUnequip();
		int index = p().relics.indexOf(r);
		this.instantObtain(p(), index != -1 ? index : p().relics.size(), true);
		if (this instanceof CounterKeeper && Loader.isModLoaded("RelicUpgradeLib") && r != null) {
			((CounterKeeper) this).run(r, this);
		}
		this.flash();
		FloatyEffect fe = ReflectionHacks.getPrivateInherited(this, getClass(), "f_effect");
		fe.x = fe.y = 0.0F;
	}

}
