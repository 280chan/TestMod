package relics;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class SaigiyounoYou extends MyRelic {
	public static final String ID = "SaigiyounoYou";
	
	public SaigiyounoYou() {
		super(ID, RelicTier.BOSS, LandingSound.CLINK);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void onAttack(final DamageInfo info, final int damage, final AbstractCreature target) {
		if (this.isActive && (target == null || !target.isPlayer) && damage > 0)
			AbstractDungeon.player.heal(1);
	}
	
}