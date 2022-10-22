package testmod.relicsup;

import com.megacrit.cardcrawl.cards.DamageInfo;

public class AntiphasicUp extends AbstractUpgradedRelic {
	
	public int onAttacked(final DamageInfo info, final int damage) {
		if (damage >= p().maxHealth / 10.0 && (damage != p().maxHealth || damage != 1)) {
			p().increaseMaxHp(5, true);
		}
		return damage;
	}

}