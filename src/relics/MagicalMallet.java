package relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import actions.MagicalMalletAction;

public class MagicalMallet extends AbstractTestRelic{
	public static final String ID = "MagicalMallet";
	
	public MagicalMallet() {
		super(ID, RelicTier.SHOP, LandingSound.MAGICAL);
		this.setTestTier(BAD);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atTurnStartPostDraw() {
		if (this.isActive)
			this.addToBot(new MagicalMalletAction(this, AbstractDungeon.player.hand.group));
    }
	
}