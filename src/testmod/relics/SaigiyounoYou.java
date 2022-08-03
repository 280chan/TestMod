package testmod.relics;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class SaigiyounoYou extends AbstractTestRelic {
	
	public SaigiyounoYou() {
		super(RelicTier.BOSS, LandingSound.CLINK);
	}
	
	public void onAttack(final DamageInfo info, final int damage, final AbstractCreature target) {
		if ((target == null || !target.isPlayer) && damage > 0)
			p().heal(1);
	}
	
}