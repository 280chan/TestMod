package relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTags;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.helpers.PowerTip;

public class HeartOfStrike extends AbstractTestRelic {
	public static final String ID = "HeartOfStrike";
	
	public HeartOfStrike() {
		super(ID, RelicTier.RARE, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	private String updateDesc() {
		return DESCRIPTIONS[0] + DESCRIPTIONS[1] + this.counter + DESCRIPTIONS[2];
	}
	
	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.updateDesc()));
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