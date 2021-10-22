package relics;

import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class IncinerationGenerator extends AbstractTestRelic {
	
	public IncinerationGenerator() {
		super(RelicTier.BOSS, LandingSound.HEAVY);
	}
	
	public void onEquip() {
		this.addEnergy();
    }
	
	public void onUnequip() {
		this.reduceEnergy();
    }
	
	public void atTurnStartPostDraw() {
		this.addToBot(new ExhaustAction(AbstractDungeon.player, AbstractDungeon.player, 1, false));
	}
	
}