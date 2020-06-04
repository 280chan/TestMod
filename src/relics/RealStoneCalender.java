package relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;

public class RealStoneCalender extends MyRelic{
	public static final String ID = "RealStoneCalender";
	
	public RealStoneCalender() {
		super(ID, RelicTier.RARE, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atBattleStart() {
		this.counter = 0;
    }
	
	public void onPlayerEndTurn() {
		this.counter++;
		int temp = this.counter * this.counter;
		if (this.counter == 7)
			this.counter = 0;
		this.addToTop(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(temp, true), DamageType.THORNS, AttackEffect.BLUNT_HEAVY));
    }
	
	public void onVictory() {
		this.counter = -1;
    }
}