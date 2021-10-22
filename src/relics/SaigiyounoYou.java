package relics;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class SaigiyounoYou extends AbstractTestRelic {
	
	public SaigiyounoYou() {
		super(RelicTier.BOSS, LandingSound.CLINK);
	}
	
	public void onAttack(final DamageInfo info, final int damage, final AbstractCreature target) {
		if ((target == null || !target.isPlayer) && damage > 0)
			AbstractDungeon.player.heal(1);
	}
	
}