package testmod.relics;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class Nine extends AbstractRevivalRelicToModifyDamage {
	public static String ID = "Nine";
	
	public Nine() {
		super(ID, RelicTier.SHOP, LandingSound.MAGICAL);
		this.setTestTier(BAD);
	}
	
	public int onAttacked(final DamageInfo info, final int damage) {
		AbstractPlayer p = AbstractDungeon.player;
		if (damage >= p.currentHealth) {
			p.maxHealth -= 9;
			if (p.maxHealth < 1)
				p.maxHealth = 1;
			if (p.currentHealth > p.maxHealth)
				p.currentHealth = p.maxHealth;
			p.healthBarUpdatedEvent();
			return 1;
		}
		return damage;
    }

	@Override
	protected int damageModifyCheck(AbstractPlayer p, DamageInfo info, int originalDamage) {
		return 1;
	}

	@Override
	protected boolean resetHpCheck(AbstractPlayer p, int damageAmount) {
		return true;
	}
	
}