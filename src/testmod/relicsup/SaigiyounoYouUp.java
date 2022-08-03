package testmod.relicsup;

import java.util.stream.Stream;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class SaigiyounoYouUp extends AbstractUpgradedRelic {
	
	public SaigiyounoYouUp() {
		super(RelicTier.BOSS, LandingSound.CLINK);
	}
	
	public void onAttack(final DamageInfo info, final int damage, final AbstractCreature t) {
		if (t != null && !t.isPlayer && Stream.of(p().currentHealth, damage, t.currentHealth).allMatch(Prime::isPrime)) {
			this.att(new InstantKillAction(t));
		}
	}
	
}