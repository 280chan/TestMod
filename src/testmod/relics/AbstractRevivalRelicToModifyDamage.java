package testmod.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.OnPlayerDeathRelic;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public abstract class AbstractRevivalRelicToModifyDamage extends AbstractTestRelic implements OnPlayerDeathRelic {

	protected int previousFuckHp = -1;
	protected boolean deathTriggered = false;
	
	public AbstractRevivalRelicToModifyDamage(String id, RelicTier tier, LandingSound sfx) {
		super(id, tier, sfx);
	}
	
	protected abstract int damageModifyCheck(AbstractPlayer p, DamageInfo info, int originalDamage);

	protected abstract boolean resetHpCheck(AbstractPlayer p, int damageAmount);
	
	public void update() {
		super.update();
		if (!this.isObtained || !this.isActive || p() == null)
			return;
		if (this.previousFuckHp > 0 && p().currentHealth < 1) {
			this.deathTriggered = true;
			int tempDmg = this.previousFuckHp - p().currentHealth;
			if (tempDmg > 1 && this.resetHpCheck(p(), tempDmg)) {
				p().currentHealth = this.previousFuckHp;
			}
		} else {
			this.previousFuckHp = p().currentHealth;
		}
	}
	
	@Override
	public boolean onPlayerDeath(AbstractPlayer p, DamageInfo info) {
		if (!this.isActive)
			return true;
		if (this.deathTriggered) {
			this.deathTriggered = false;
			int tmp = this.damageModifyCheck(p, info, this.previousFuckHp - p.currentHealth);
			if (tmp < info.output)
				this.show();
			p.currentHealth = this.previousFuckHp - tmp;
			if (p.currentHealth > 0) {
				return false;
			}
		}
		return true;
	}

}
