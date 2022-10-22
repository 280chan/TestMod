package testmod.relics;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.powers.ThornsPower;

public class BouquetWithThorns extends AbstractTestRelic {
	
	private void increaseThorns(int amount) {
		if (amount > 0) {
			this.flash();
			this.att(apply(p(), new ThornsPower(p(), amount)));
		}
	}
	
	public void atPreBattle() {
		increaseThorns(1);
	}
	
	public int onAttacked(final DamageInfo info, final int damage) {
		if (this.inCombat() && info.type != DamageType.THORNS) {
			increaseThorns(info.type == DamageType.NORMAL ? (damage > 0 ? 2 : 1) : damage);
		}
		return damage;
	}
	
}