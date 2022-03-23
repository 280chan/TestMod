package testmod.relics;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTags;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.helpers.PowerTip;

import testmod.powers.AbstractTestPower;

public class HeartOfStrike extends AbstractTestRelic {
	
	private HeartOfStrikePower hosp;
	
	public HeartOfStrike() {
		super(RelicTier.RARE, LandingSound.HEAVY);
		this.counter = 2;
	}
	
	public void updateDescription(PlayerClass c) {
		this.tips.clear();
		if (this.counter > 2)
			this.tips.add(new PowerTip(this.name, DESCRIPTIONS[0] + DESCRIPTIONS[1] + this.counter + DESCRIPTIONS[2]));
		else
			this.tips.add(new PowerTip(this.name, DESCRIPTIONS[0]));
	    initializeTips();
	}
	
	public void atPreBattle() {
		this.counter = 2;
		this.p().powers.add(hosp = new HeartOfStrikePower());
	}
	
	public void onExhaust(AbstractCard c) {
		if (c.hasTag(CardTags.STRIKE)) {
			this.counter++;
			this.updateDescription(p().chosenClass);
		}
	}

	public void onVictory() {
		this.counter = 2;
		this.updateDescription(p().chosenClass);
	}
	
	public void onEquip() {
		if (this.inCombat()) {
			this.atPreBattle();
		}
	}
	
	public void onUnequip() {
		if (p() != null && p().powers != null && p().powers.stream().anyMatch(p -> p.equals(hosp))) {
			p().powers.remove(hosp);
		}
	}
	
	private class HeartOfStrikePower extends AbstractTestPower implements InvisiblePower {
		public HeartOfStrikePower() {
			super("HeartOfStrike");
			this.owner = p();
			this.addMapWithSkip(p -> (hosp = new HeartOfStrikePower()));
		}
		
		public float atDamageFinalGive(float damage, DamageType type, AbstractCard c) {
			return c.hasTag(CardTags.STRIKE) ? damage * counter : damage;
		}
	}

}