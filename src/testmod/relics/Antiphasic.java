package testmod.relics;

import com.megacrit.cardcrawl.cards.DamageInfo;

public class Antiphasic extends AbstractTestRelic {
	
	public Antiphasic() {
		super(RelicTier.COMMON, LandingSound.MAGICAL, BAD);
	}
	
	public int onAttacked(final DamageInfo info, final int damage) {
		if (damage >= p().maxHealth / 4.0 && (damage != p().maxHealth || damage != 1)) {
	    	p().increaseMaxHp(5, true);
    	}
		return damage;
    }

}