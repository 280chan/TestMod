package relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTags;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.helpers.PowerTip;

public class HeartOfStrike extends AbstractTestRelic {
	
	public HeartOfStrike() {
		super(RelicTier.RARE, LandingSound.HEAVY);
	}
	
	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, DESCRIPTIONS[0] + DESCRIPTIONS[1] + this.counter + DESCRIPTIONS[2]));
	    initializeTips();
	}
	
	public void atPreBattle() {
		this.counter = 2;
	}
	
	public void onExhaust(AbstractCard c) {
		if (c.hasTag(CardTags.STRIKE)) {
			this.counter++;
			this.updateDescription(null);
		}
	}
	
	public float atDamageModify(float damage, AbstractCard c) {
		return c.hasTag(CardTags.STRIKE) ? damage * this.counter : damage;
	}
	
	public void onVictory() {
		this.counter = -1;
	}

}