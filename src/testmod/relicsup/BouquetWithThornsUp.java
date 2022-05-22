package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.powers.ThornsPower;

public class BouquetWithThornsUp extends AbstractUpgradedRelic {
	
	public BouquetWithThornsUp() {
		super(RelicTier.RARE, LandingSound.CLINK);
	}
	
	private void increaseThorns(int amount) {
		if (amount > 0) {
			this.flash();
			this.addToTop(apply(p(), new ThornsPower(p(), amount)));
		}
	}
	
	public void atPreBattle() {
		increaseThorns(1);
    }
	
	private int calculateAmount(DamageInfo info) {
		if (info.owner == null)
			return 1;
		return Math.max(1, (int) info.owner.powers.stream().filter(p -> !(p instanceof InvisiblePower)).count());
	}
	
	public int onAttacked(DamageInfo info, int damage) {
		if (this.inCombat()) {
			increaseThorns(info.type == DamageType.NORMAL ? calculateAmount(info) : damage);
		}
		return damage;
    }
	
}