package relics;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BalancedPeriapt extends AbstractTestRelic {
	
	public BalancedPeriapt() {
		super(RelicTier.UNCOMMON, LandingSound.FLAT, BAD);
	}
	
	public float preChangeMaxHP(float amount) {
		AbstractPlayer p = AbstractDungeon.player;
		if (p.isDead || p.isDying)
			return 0;
		float tmp = amount * 3;
		if (amount < 0) {
			p.damage(new DamageInfo(p, (int)(-amount), DamageType.HP_LOSS));
		} else if (amount > 0) {
			p.heal((int)tmp);
		}
    	return 0;
    }
	
}