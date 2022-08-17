package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTags;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import testmod.powers.AbstractTestPower;

public class HeartOfStrikeUp extends AbstractUpgradedRelic {
	private HeartOfStrikePowerUp hosp;
	
	public String getUpdatedDescription() {
		return this.counter > 2 ? DESCRIPTIONS[0] + DESCRIPTIONS[1] + this.counter + DESCRIPTIONS[2] : DESCRIPTIONS[0];
	}
	
	public void atPreBattle() {
		this.counter = 2;
		this.p().powers.add(hosp = new HeartOfStrikePowerUp());
		this.combatCards().forEach(c -> c.tags.add(CardTags.STRIKE));
	}
	
	public void onExhaust(AbstractCard c) {
		if (c.hasTag(CardTags.STRIKE)) {
			this.counter += c.tags.stream().filter(CardTags.STRIKE::equals).count();
			this.updateDescription();
		}
	}

	public void onVictory() {
		this.counter = 2;
		this.updateDescription();
	}
	
	public void onEquip() {
		this.counter = 2;
		if (this.inCombat()) {
			this.atPreBattle();
		}
	}
	
	public void onUnequip() {
		if (p() != null && p().powers != null && p().powers.stream().anyMatch(p -> p.equals(hosp))) {
			p().powers.remove(hosp);
		}
	}
	
	private class HeartOfStrikePowerUp extends AbstractTestPower implements InvisiblePower {
		public HeartOfStrikePowerUp() {
			this.owner = p();
			this.addMapWithSkip(p -> (hosp = new HeartOfStrikePowerUp()));
		}
		
		private float dmg(float damage) {
			return damage * counter;
		}
		
		public float atDamageFinalGive(float damage, DamageType type, AbstractCard c) {
			return c.tags.stream().filter(CardTags.STRIKE::equals).map(t -> get(this::dmg)).reduce(t(), this::chain)
					.apply(damage);
		}
	}

}