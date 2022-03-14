package testmod.relics;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class Antiphasic extends AbstractTestRelic {
	
	public Antiphasic() {
		super(RelicTier.COMMON, LandingSound.MAGICAL, BAD);
	}
	
	public int onAttacked(final DamageInfo info, final int damage) {
        if (damage >= AbstractDungeon.player.maxHealth / 4.0) {
        	if (damage == AbstractDungeon.player.maxHealth && damage == 1) {
        		return damage;
        	}
        	AbstractDungeon.player.increaseMaxHp(5, true);
        }
		return damage;
    }

}